val myCompileVersion: String by project
val myMinVersion: String by project
val myTargetVersion: String by project
val myCodeVersion: String by project
val myVersionName: String by project
val navigationVersion: String by project
plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("com.google.devtools.ksp") version "1.7.0-1.0.6"
}

android {
  this.compileSdk = myCompileVersion.toInt()
  defaultConfig {
    applicationId = "small.app.shopping.list"
    minSdk = myMinVersion.toInt()
    targetSdk = myTargetVersion.toInt()
    versionCode = myCodeVersion.toInt()
    versionName = myVersionName

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // The following argument makes the Android Test Orchestrator run its
    // "pm clear" command after each test invocation. This command ensures
    // that the app's state is completely cleared between tests.
    testInstrumentationRunnerArguments["clearPackageData"] = "true"
    testOptions {
      execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

  }

  buildFeatures {
    dataBinding = true
// for view binding:
    viewBinding = true
  }

  buildTypes {
    getByName("release") {
      // Enables code shrinking, obfuscation, and optimization for only
      // your project"s release build type.
      isMinifyEnabled = true

      // Enables resource shrinking, which is performed by the
      // Android Gradle plugin.
      isShrinkResources = false // Need notification_action.xml

      // Includes the default ProGuard rules files that are packaged with
      // the Android Gradle plugin. To learn more, go to the section about
      // R8 configuration files.
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }

  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
  namespace = "small.app.shopping.list"
}

dependencies {

  implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  implementation("androidx.core:core-ktx:1.9.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.8.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.legacy:legacy-support-v4:1.0.0")
  implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
  implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")


  androidTestImplementation("androidx.test:runner:1.5.2")

  androidTestImplementation("com.adevinta.android:barista:4.2.0") {
    exclude(group = "org.jetbrains.kotlin") // Only if you already use Kotlin in your project
  }
  //Lifecycle
  val lifeCycleVersion = "2.4.1"
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifeCycleVersion")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifeCycleVersion")
  androidTestImplementation("androidx.test:rules:1.5.0")

  //Room
  val roomVersion = "2.4.2"
  annotationProcessor("androidx.room:room-compiler:$roomVersion")
  implementation("com.google.devtools.ksp:symbol-processing-api:1.7.0-1.0.6")

  implementation("androidx.room:room-runtime:$roomVersion")
  ksp("androidx.room:room-compiler:$roomVersion")
  // optional - Kotlin Extensions and Coroutines support for Room
  implementation("androidx.room:room-ktx:$roomVersion")

  // optional - Test helpers
  testImplementation("androidx.room:room-testing:$roomVersion")

  //Coroutine
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

  //Needed for espresso
  androidTestImplementation("androidx.fragment:fragment-testing:1.5.5")

  //Manage Json
  implementation("com.google.code.gson:gson:2.8.9")

  //Test
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation("androidx.test:core-ktx:1.5.0")

}