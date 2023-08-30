package com.trifonov.findmyhouse.data

import kotlinx.serialization.Serializable

@Serializable
data class FeatureCollection(
    val type: String?,
    val version: String?,
    val features: List<Feature>?,
    val attribution: String?,
    val licence: String?,
    val query: String?,
    val filters: Filters?
)

@Serializable
data class Feature(
    val type: String?,
    val geometry: Geometry,
    val properties: Properties
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

@Serializable
data class Properties(
    val label: String?,
    val score: Double?,
    val housenumber: String?,
    val id: String?,
    val name: String?,
    val postcode: String?,
    val citycode: String?,
    val x: Double?,
    val y: Double?,
    val city: String?,
    val district: String?,
    val context: String?,
    val type: String?,
    val importance: Double?,
    val street: String?
)

@Serializable
data class Filters(
    val type: String?
)