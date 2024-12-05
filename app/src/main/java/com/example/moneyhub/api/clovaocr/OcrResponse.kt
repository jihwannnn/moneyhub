package com.example.moneyhub.api.clovaocr

data class OcrResponse(
    val images: List<ImageResponse>
)

data class ImageResponse(
    val fields: List<TextField>
)

data class TextField(
    val inferText: String,
    val boundingPoly: List<Coordinate>
)

data class Coordinate(
    val x: Int,
    val y: Int
)
