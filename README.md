

# Babysitter App

## Overview
The Babysitter app is designed to connect parents with babysitters. This app allows parents to find babysitters based on various criteria like location, experience, and hourly wage. Babysitters can register and offer their services, setting up profiles that include their details and preferences.

## Features
- **User Registration and Login**: Separate registration processes for babysitters and parents. Users can log in to their accounts to access their profiles and settings.
- **Profile Management**: Users can update their profiles, including contact information, services offered, and availability.
- **Search and Filter**: Parents can search for babysitters using filters like location, experience, and hourly wage.
- **Real-time Chat**: Once connected, parents and babysitters can communicate through a built-in messaging system.
- **Location Services**: Integration with Google Maps to help users find babysitters near their location.

## Technical Details
- **Platform**: Android
- **Languages**: Java
- **Database**:  Realtime Database for storing user data and chat messages.
- **APIs**: Google Maps API for location services.

## Setup and Configuration

### 1. Google Maps Setup
1. Enable Google Maps API in the Google Cloud Console.
2. Obtain and configure an API key in your Android project to use Google Maps features.

### 2. Android Project Configuration
1. Import the project into Android Studio.
2. Configure `build.gradle` with dependencies for Firebase, Google Maps, and other required libraries.
3. Ensure the Android manifest includes permissions for internet and location services.

### 3. Running the Application
1. Build the application in Android Studio and run it on an emulator or a physical device.
2. Register as a new user (either as a parent or a babysitter) and explore the features.

## Common Issues and Troubleshooting
- **API Key Restrictions**: If Google Maps features do not work, verify that the API key is correctly set up in the Google Cloud Console and is not restricted.
- **Authentication Errors**: Ensure that Firebase Authentication is properly set up in the Firebase console, and the app’s configuration matches the settings.

## Future Enhancements
- **Advanced Filtering Options**: Implement more advanced filters for searching babysitters based on additional criteria like certifications or special needs experience.
- **Scheduling and Calendar Integration**: Add a feature to schedule and manage appointments directly through the app, including integration with external calendars.
- **Ratings and Reviews**: Users can rate and review babysitters after availing their services.
- **Payment Integration**: Integrate a payment gateway to handle transactions directly through the app, providing a seamless experience for booking and payments.

## Mockups
Here are some screenshots and mockups of the application:

![Mockup 1](https://github.com/NoaGilboa/Babysitter1/assets/143444119/63d8b860-8fae-44d5-9f82-d12658c44656)
![Mockup 2](https://github.com/NoaGilboa/Babysitter1/assets/143444119/3defa6c2-09f0-46cc-bdb7-c4942fac862d)
![Mockup 3](https://github.com/NoaGilboa/Babysitter1/assets/143444119/f8398cd4-b8bf-4770-99d6-575136cb3555)
![Mockup 4](https://github.com/NoaGilboa/Babysitter1/assets/143444119/2bc31e99-5eb2-4540-a4ce-cdfd86215e19)
![Mockup 5](https://github.com/NoaGilboa/Babysitter1/assets/143444119/5d82da94-307b-4f27-98ad-4a3b30358abd)
![Mockup 6](https://github.com/NoaGilboa/Babysitter1/assets/143444119/3ae54dd9-4746-4e5e-bbf5-6b783fdab7ee)
![Mockup 7](https://github.com/NoaGilboa/Babysitter1/assets/143444119/02b6dc8a-bd07-43d2-8997-2b12cea7854f)

## Video Demonstration
Watch a video demonstration of the Babysitter app:

[![Watch the video](https://github.com/NoaGilboa/Babysitter1/assets/143444119/f4b01abc-a3ae-4e6d-af1d-ff4e4f75433f)](https://github.com/NoaGilboa/Babysitter1/assets/143444119/f4b01abc-a3ae-4e6d-af1d-ff4e4f75433f)

