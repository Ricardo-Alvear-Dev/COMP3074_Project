plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false  // Ensure version is 1.9.24 or higher
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false // Must match Kotlin version prefix
}