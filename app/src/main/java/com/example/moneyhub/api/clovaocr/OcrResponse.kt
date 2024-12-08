package com.example.moneyhub.api.clovaocr

data class OcrResponse(
    val images: List<ImageResponse>
)

data class ImageResponse(
    val fields: List<TextField>
)

data class TextField(
    val inferText: String,
    val boundingPoly: BoundingPoly
)

data class BoundingPoly(
    val vertices: List<Coordinate>
)

data class Coordinate(
    val x: Float,
    val y: Float
)
