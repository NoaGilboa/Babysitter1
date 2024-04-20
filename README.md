# 24A10357-Noa-Gilboa-final-project---Babysitter

Babysitter App Documentation
Overview:
The Babysitter app is designed to connect parents with babysitters. This app allows parents to find babysitters based on various criteria like location, experience, and hourly wage. Babysitters can register and offer their services, setting up profiles that include their details and preferences.

Features:
•	User Registration and Login: Separate registration processes for babysitters and parents. Users can log in to their accounts to access their profiles and settings.
•	Profile Management: Users can update their profiles, including contact information, services offered, and availability.
•	Search and Filter: Parents can search for babysitters using filters like location, experience, and hourly wage.
•	Real-time Chat: Once connected, parents and babysitters can communicate through a built-in messaging system.
•	Location Services: Integration with Google Maps to help users find babysitters near their location.
Technical Details:
•	Platform: Android
•	Languages: Java
•	Database: Firebase Realtime Database for storing user data and chat messages.
•	Authentication: Firebase Authentication is used for handling user authentication.
•	APIs: Google Maps API for location services.
Setup and Configuration:
1.	Firebase Setup:
o	Create a Firebase project in the Firebase console.
o	Enable Firebase Authentication and configure it to use email and password authentication.
o	Set up Firebase Realtime Database with read/write permissions for authenticated users.
2.	Google Maps Setup:
o	Enable Google Maps API in the Google Cloud Console.
o	Obtain and configure an API key in your Android project to use Google Maps features.
3.	Android Project Configuration:
o	Import the project into Android Studio.
o	Configure build.gradle with dependencies for Firebase, Google Maps, and other required libraries.
o	Ensure the Android manifest includes permissions for internet and location services.
4.	Running the Application:
o	Build the application in Android Studio and run it on an emulator or a physical device.
o	Register as a new user (either as a parent or a babysitter) and explore the features.
Common Issues and Troubleshooting:
•	Firebase Database Rules: If read/write operations fail, ensure that Firebase Database rules are set correctly to allow access to authenticated users.
•	API Key Restrictions: If Google Maps features do not work, verify that the API key is correctly set up in the Google Cloud Console and is not restricted.
•	Authentication Errors: Ensure that Firebase Authentication is properly set up in the Firebase console, and the app’s configuration matches the settings.
Future Enhancements:
•	Advanced Filtering Options: Implement more advanced filters for searching babysitters based on additional criteria like certifications or special needs experience.
•	Scheduling and Calendar Integration: Add a feature to schedule and manage appointments directly through the app, including integration with external calendars.
•	Ratings and Reviews: Users can rate and review babysitters after availing their services.
•	Payment Integration: Integrate a payment gateway to handle transactions directly through the app, providing a seamless experience for booking and payments.
This documentation provides a basic framework for setting up and using the Babysitter app. For detailed code references or further assistance, refer to the source files provided in the app’s project directory or contact with me.
