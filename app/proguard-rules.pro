# Project specific ProGuard / R8 rules for WII To-Do production build

# Keep Kotlin Coroutines
-keepattributes *Annotation*,InnerClasses,EnclosingMethod
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep Room Database models & DAOs
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Keep Data models
-keep class com.example.data.model.** { *; }
-keep class com.example.data.local.entity.** { *; }

# Keep Google GenAI & Firebase models
-keepattributes Signature
-keepclassmembers enum * { *; }
-dontwarn com.google.firebase.**
