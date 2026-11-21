# Keep Room entities/DAOs
-keep class com.allinone.app.data.db.entity.** { *; }
-keep class com.allinone.app.data.db.dao.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Media3 / ExoPlayer
-dontwarn androidx.media3.**

# CameraX
-dontwarn androidx.camera.**

# Keep model classes used for backup serialization
-keep class com.allinone.app.util.model.** { *; }
