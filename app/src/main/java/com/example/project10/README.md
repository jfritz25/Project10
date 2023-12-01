# Project_10 - Sensor and Gesture App

*Ashley Steitz and Jacob Fritz collaborated on this project.*

---

**Description**

In Project 10, we developed an Android app using Jetpack Compose that showcases the functionality of gesture and sensors.
The app consists of two activities: Sensor Activity and Gesture Activity.

In Sensor Activity:
- Displays the user's name, location (state and city), current temperature, and the value of another chosen sensor (excluding Accelerometer and Light sensor).
- Includes a button "Gesture Playground" that supports fling operation, navigating the user to Gesture Activity upon performing a fling operation.
- Utilizes the Geocoder class to retrieve location information.

In Gesture Activity:
- Divides the activity into two fragments.
- The top fragment contains a red ball that moves with user gestures.
- The bottom fragment logs the activities performed in the top fragment.
- The red ball responds to user swipes, moving accordingly.
- Records and updates a log of gestures performed by the user.

Bonus 10%:
- Created a third activity replicating Gesture Activity, using a sensor (e.g., accelerometer) to move the ball within the fragment.

**Functionality**
[Opened the app]
[Swipped Down]
[Swipped Left]
[Swipped Right]
[Swipped Up]
[All gestures populated the screen]

---

## Video Walkthrough

Watch a demonstration of the app's functionality in the GIF available on [GitHub](https://github.com/jfritz25/Project8ThisIsTheOne/blob/master/app/src/main/java/com/example/project8/convert.gif).
*GIF created with [CloudConvert](https://cloudconvert.com/).*

**UI Challenges:**
- Handling the live data
- Adjusting to Jetpack Compose

**Backend Challenges:**
- Learned how to work with the position and location functions.
- A lot of new content (gestures, lat, long, etc...) that was difficult to integrate using the source links at first but made sense after whiteboarding and lecture.

---

## License

    Copyright [2023] [Ashley Steitz, Jacob Fritz]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


