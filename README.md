[![CodeStyle](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![Kotlin Version](https://img.shields.io/badge/kotlin-1.4.10-blue.svg)](http://kotlinlang.org/)
[![Gradle](https://lv.binarybabel.org/catalog-api/gradle/latest.svg)](https://lv.binarybabel.org/catalog/gradle/latest)
[![API](https://img.shields.io/badge/API-21%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)](http://www.apache.org/licenses/LICENSE-2.0)

 Noted is a note taking app, that's inspired by - Google Keep. The Application leverages the latest improvement in the android ecosystem and improved on the parent application
 with extended functionalities such as vault-like behaviour which allows user's to add security to notes by using a user-generated password as key.

 Minimum Api Level : 21 compileSdkVersion : 30

 Build System : [Gradle](https://gradle.org/)
 
 <img src="https://github.com/chydee/Noted/blob/master/screens/noted_home.png" width="200" title="Home Screen"> <img src="screens/noted_add_note.png" width="200" title="Create new note"> <img src="https://github.com/chydee/Noted/blob/master/screens/noted_new_note.png" width="200" title="note">
 <img src="screens/noted_home_notes.png" width="200" title=""> <img src="screens/noted_side_nav.png" width="200" title=""> <img src="screens/note_trash.png" width="200" title="">

 ## Table of Contents

-   [Installation](#installation)
-   [Design](#design)
-   [Architecture](#architecture)
-   [Libraries](#libraries)
-   [Credits](#credits)

 ## Installation
    Follow these steps if you want to get a local copy of the project.

    Prerequisites
    Android Studio IDE 3.0+
    Android SDK v28
    Android Build Tools v28.0.3
    Gradle 4.10.1
    1. Clone or fork the repository (Master Branch) by running the command below
    on your git terminal

    git clone https://github.com/chydee/Noted.git
    2. Import the project in AndroidStudio, and add API Key
    In Android Studio, go to File -> New -> Import project
    Follow the dialog for set up instructions


 ## Design

 No design rules was followed in particular but I ensured App [support different screen sizes](https://developer.android.com/training/multiscreen/screensizes) and the content has been adapted to fit for mobile devices. To do that, create a flexible layout using one or more of the following concepts:

 -   [Use constraintLayout](https://developer.android.com/training/multiscreen/screensizes#ConstraintLayout)
 -   [Avoid hard-coded layout sizes](https://developer.android.com/training/multiscreen/screensizes#TaskUseWrapMatchPar)

 ## Architecture

 The architecture of the application is based, apply and strictly complies with each of the following 4 points:

 -   A single-activity architecture, using the [Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started) to manage fragment operations.
 -   [Android architecture components](https://developer.android.com/topic/libraries/architecture/), part of Android Jetpack for give to project a robust design, testable and maintainable.
 -   Pattern [Model-View-ViewModel](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) (MVVM) facilitating a [separation](https://en.wikipedia.org/wiki/Separation_of_concerns) of development of the graphical user interface.
 -   [S.O.L.I.D](https://en.wikipedia.org/wiki/SOLID) design principles intended to make software designs more understandable, flexible and maintainable.

 ## Libraries

     -   [Jetpack](https://developer.android.com/jetpack):
     -   [Android KTX](https://developer.android.com/kotlin/ktx.html) - provide concise, idiomatic Kotlin to Jetpack and Android platform APIs.
     -   [AndroidX](https://developer.android.com/jetpack/androidx) - major improvement to the original Android [Support Library](https://developer.android.com/topic/libraries/support-library/index), which is no longer maintained.
     -   [Data Binding](https://developer.android.com/topic/libraries/data-binding/) - allows you to bind UI components in your layouts to data sources in your app using a declarative format rather than programmatically.
     -   [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - allows you to more easily write code that interacts with views.
     -   [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) - perform actions in response to a change in the lifecycle status of another component, such as activities and fragments.
     -   [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - lifecycle-aware, meaning it respects the lifecycle of other app components, such as activities, fragments, or services.
     -   [Navigation](https://developer.android.com/guide/navigation/) - helps you implement navigation, from simple button clicks to more complex patterns, such as app bars and the navigation drawer.
     -   [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - designed to store and manage UI-related data in a lifecycle conscious way. The ViewModel class allows data to survive configuration changes such as screen rotations.
 -   [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - managing background threads with simplified code and reducing needs for callbacks.
 -   [and more...]

     ### Test Libraries
     -   [Espresso](https://developer.android.com/training/testing/espresso) - to write concise, beautiful, and reliable Android UI tests
     -   [JUnit](https://github.com/junit-team/junit4) - a simple framework to write repeatable tests. It is an instance of the xUnit architecture for unit testing frameworks.
     -   [AndroidX](https://github.com/android/android-test) - the androidx test library provides an extensive framework for testing Android apps.

     ### Plugins

     -   [Firebase-Crashlytics]   


 ## Credits

 * The preview images were created using 'Previewed' at [Previewed](https://previewed.app/)
 * The illustration images were provided by 'UnDraw' at [UnDraw](https://undraw.co/illustrations)
 * The icons and application icons were created and downloaded at [FlatIcons](https://flaticons.com/)



 ```license
 Copyright 2021 Desmond Ngwuta

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ```


