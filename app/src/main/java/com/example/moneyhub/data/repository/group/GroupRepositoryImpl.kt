package com.example.moneyhub.data.repository.group


import kotlinx.coroutines.tasks.await

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.QuerySnapshot

import com.example.moneyhub.model.CurrentUser
import com.example.moneyhub.model.Membership
import com.example.moneyhub.model.Role
import com.example.moneyhub.model.UserGroup
import javax.inject.Inject


class GroupRepositoryImpl @Inject constructor() : GroupRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun createUserGroup(uid: String): Result<Unit> {
        return try {
            // Create a new UserGroup document with empty groups map
            val userGroupData = mapOf(
                "uid" to uid,
                "groups" to emptyMap<String, String>()
            )

            // Save to Firestore
            db.collection("userGroups")
                .document(uid)
                .set(userGroupData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createGroup(name: String, user: CurrentUser): Result<Unit> {
        return try {
            // 동일한 이름이 있는지 확인
            val existingGroups = db.collection("groups")
                .whereEqualTo("name", name)
                .get()
                .await()

            if (!existingGroups.isEmpty) {
                throw Exception("이미 존재하는 그룹 이름입니다")
            }

            // 그룹 문서 참조 생성
            val groupRef = db.collection("groups").document()
            val gid = groupRef.id

            // 멤버십
            val membershipRef = db.collection("members_group")
                .document(gid)
                .collection("members")
                .document(user.id)

            //  유저 그룹
            val userGroupRef = db.collection("userGroups").document(user.id)

            // 그룹 데이터 생성
            val groupData = mapOf(
                "gid" to gid,
                "name" to name,
                "inviteCode" to gid,
                "ownerId" to user.id,
                "ownerName" to (user.name),
                "memberCount" to 1,
                "createdAt" to System.currentTimeMillis()
            )

            // 멤버십 데이터 생성
            val membershipData = mapOf(
                "uid" to user.id,
                "gid" to gid,
                "userName" to user.name,
                "role" to Role.OWNER.name,
            )

            // 사용자의 그룹 데이터 업데이트
            val userGroupUpdate = mapOf(
                "groups" to mapOf(gid to name)
            )

            // 트랜잭션으로 모든 작업 수행
            db.runTransaction { transaction ->

                // 그룹 저장
                transaction.set(groupRef, groupData)

                // 멤버쉽 저장
                transaction.set(membershipRef, membershipData)

                // 사용자의 그룹 목록 업데이트
                transaction.set(userGroupRef, userGroupUpdate, SetOptions.merge())
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinGroup(
        gid: String,
        user: CurrentUser,
    ): Result<Boolean> {
        return try {
            // 그룹 정보 가져오기
            val groupDoc = db.collection("groups").document(gid).get().await()
            val groupData = groupDoc.data ?: throw Exception("그룹을 찾을 수 없습니다.")

            // 초대 코드 확인
            if (groupData["inviteCode"] != gid) {
                return Result.success(false)
            }

            // 멤버십 참조 생성
            val membershipRef = db.collection("members_group")
                .document(gid)
                .collection("members")
                .document(user.id)

            // 유저 그룹 참조
            val userGroupRef = db.collection("userGroups").document(user.id)

            // 멤버십 데이터 생성
            val membershipData = mapOf(
                "uid" to user.id,
                "gid" to gid,
                "userName" to user.name,
                "role" to Role.REGULAR.name
            )

            // 사용자의 그룹 데이터 업데이트
            val userGroupUpdate = mapOf(
                "groups" to mapOf(gid to groupData["name"])
            )

            // 트랜잭션으로 멤버 추가
            db.runTransaction { transaction ->
                transaction.set(membershipRef, membershipData)
                transaction.update(groupDoc.reference, "memberCount", (groupData["memberCount"] as Long) + 1)
                transaction.set(userGroupRef, userGroupUpdate, SetOptions.merge())
            }.await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteGroup(gid: String): Result<Unit> {
        return try {
            // 1. 사전에 모든 데이터 가져오기
            val membersSnapshot = db.collection("members_group")
                .document(gid)
                .collection("members")
                .get()
                .await()

            val postsSnapshot = db.collection("posts_group")
                .document(gid)
                .collection("posts")
                .get()
                .await()

            // 2. 모든 게시글의 댓글 가져오기
            val commentsSnapshots = mutableMapOf<String, QuerySnapshot>()
            for (postDoc in postsSnapshot.documents) {
                val commentsSnapshot = db.collection("comments_group")
                    .document(gid)
                    .collection("comments_post")
                    .document(postDoc.id)
                    .collection("comments")
                    .get()
                    .await()
                commentsSnapshots[postDoc.id] = commentsSnapshot
            }

            // 3. 트랜잭션으로 모든 데이터 삭제
            db.runTransaction { transaction ->
                // 3.1. 각 멤버의 userGroups에서 그룹 제거
                for (memberDoc in membersSnapshot.documents) {
                    val memberUid = memberDoc.getString("uid") ?: continue
                    val userGroupRef = db.collection("userGroups").document(memberUid)

                    val userGroupDoc = transaction.get(userGroupRef)
                    val currentGroups = (userGroupDoc.get("groups") as? Map<String, String>)?.toMutableMap()
                        ?: mutableMapOf()

                    currentGroups.remove(gid)
                    transaction.set(userGroupRef, mapOf("uid" to memberUid, "groups" to currentGroups))
                }

                // 3.2. 게시글과 댓글 삭제
                for (postDoc in postsSnapshot.documents) {
                    // 미리 가져온 댓글들 삭제
                    val comments = commentsSnapshots[postDoc.id]?.documents ?: continue
                    for (commentDoc in comments) {
                        transaction.delete(commentDoc.reference)
                    }

                    transaction.delete(db.collection("comments_group")
                        .document(gid)
                        .collection("comments_post")
                        .document(postDoc.id)
                    )

                    transaction.delete(postDoc.reference)
                }

                // 3.3. 멤버십 문서들 삭제
                for (doc in membersSnapshot.documents) {
                    transaction.delete(doc.reference)
                }

                // 3.4. 상위 컬렉션 문서들 삭제
                transaction.delete(db.collection("members_group").document(gid))
                transaction.delete(db.collection("posts_group").document(gid))
                transaction.delete(db.collection("comments_group").document(gid))
                transaction.delete(db.collection("groups").document(gid))
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserGroups(uid: String): Result<UserGroup> {
        return try {
            val userGroupData = db.collection("userGroups")
                .document(uid)
                .get()
                .await()

            val group = userGroupData.data?.let { UserGroup.fromMap(it)} ?: throw Exception("유저 그룹 정보를 찾을 수 없습니다")
            Result.success(group)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserMembership(gid: String, uid: String): Result<Membership>{
        return try {

            val membershipDoc = db.collection("members_group")
                .document(gid)
                .collection("members")
                .document(uid)
                .get()
                .await()

            val membership = membershipDoc.data?.let { Membership.fromMap(it) } ?: throw Exception("유저 멤버쉽 정보를 찾을 수 없습니다")
            Result.success(membership)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGroupMembers(gid: String): Result<List<Membership>> {
        return try {
            // 그룹의 모든 멤버 가져오기
            val membersSnapshot = db.collection("members_group")
                .document(gid)
                .collection("members")
                .get()
                .await()

            // 멤버십 객체 리스트로 변환
            val members = membersSnapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                Membership.fromMap(data)
            }

            Result.success(members)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun promoteMember(membership: Membership, user: CurrentUser): Result<Unit> {
        return try {

            if (user.role != Role.OWNER) {
                return Result.failure(Exception("권한이 없습니다. 소유자만 멤버를 승급시킬 수 있습니다."))
            }
            // 멤버 문서 참조
            val memberRef = db.collection("members_group")
                .document(membership.gid)
                .collection("members")
                .document(membership.uid)

            // 현재 멤버 데이터 가져오기
            val memberDoc = memberRef.get().await()
            val currentRole = Role.fromName(memberDoc.getString("role") ?: "REGULAR")

            // 새로운 역할 결정
            val newRole = when (currentRole) {
                Role.REGULAR -> Role.MANAGER
                Role.MANAGER -> Role.OWNER
                Role.OWNER -> return Result.failure(Exception("이미 최고 권한입니다"))
            }

            // 만약 새로운 역할이 OWNER라면
            if (newRole == Role.OWNER) {
                // 트랜잭션으로 역할 교체
                db.runTransaction { transaction ->
                    // 현재 OWNER(currentUser)를 MANAGER로 강등
                    val currentOwnerRef = db.collection("members_group")
                        .document(membership.gid)
                        .collection("members")
                        .document(user.id)
                    transaction.update(currentOwnerRef, "role", Role.MANAGER.name)

                    // 새로운 멤버를 OWNER로 승급
                    transaction.update(memberRef, "role", newRole.name)

                    // 그룹 문서의 owner 정보 업데이트
                    val groupRef = db.collection("groups").document(membership.gid)
                    transaction.update(
                        groupRef, mapOf(
                            "ownerId" to membership.uid,
                            "ownerName" to membership.userName
                        )
                    )
                }.await()
            } else {
                // MANAGER로의 승급은 단순 업데이트
                memberRef.update("role", newRole.name).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun demoteMember(membership: Membership): Result<Unit> {
        return try {
            // 멤버 문서 참조
            val memberRef = db.collection("members_group")
                .document(membership.gid)
                .collection("members")
                .document(membership.uid)

            // 현재 멤버 데이터 가져오기
            val memberDoc = memberRef.get().await()
            val currentRole = Role.fromName(memberDoc.getString("role") ?: "REGULAR")

            // 새로운 역할 결정
            val newRole = when (currentRole) {
                Role.OWNER -> return Result.failure(Exception("소유자는 강등할 수 없습니다"))
                Role.MANAGER -> Role.REGULAR
                Role.REGULAR -> return Result.failure(Exception("이미 최하위 권한입니다"))
            }

            // 역할 업데이트
            memberRef.update("role", newRole.name).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun leaveGroup(gid: String, user: CurrentUser): Result<Unit> {
        return try {
            // 멤버 문서 참조
            val memberRef = db.collection("members_group")
                .document(gid)
                .collection("members")
                .document(user.id)

            // 현재 멤버 데이터 확인
            val memberDoc = memberRef.get().await()
            val currentRole = Role.fromName(memberDoc.getString("role") ?: "REGULAR")

            // OWNER는 그룹을 떠날 수 없음
            if (currentRole == Role.OWNER) {
                return Result.failure(Exception("소유자는 그룹을 떠날 수 없습니다. 소유권을 이전하거나 그룹을 삭제해주세요."))
            }

            // 그룹 문서 참조
            val groupRef = db.collection("groups").document(gid)

            // 사용자의 그룹 목록 참조
            val userGroupRef = db.collection("userGroups").document(user.id)

            // 트랜잭션으로 처리
            db.runTransaction { transaction ->
                // 그룹의 멤버 수 감소
                val groupDoc = transaction.get(groupRef)
                val currentMemberCount = groupDoc.getLong("memberCount") ?: 1
                transaction.update(groupRef, "memberCount", currentMemberCount - 1)

                // 멤버십 삭제
                transaction.delete(memberRef)

                // 사용자의 그룹 목록에서 제거
                val userGroupDoc = transaction.get(userGroupRef)
                val currentGroups = (userGroupDoc.get("groups") as? Map<String, String>)?.toMutableMap()
                    ?: mutableMapOf()
                currentGroups.remove(gid)
                transaction.update(userGroupRef, "groups", currentGroups)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}