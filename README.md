# MIS-CaseStudy

****Project Overview****
The Park & Ride application allows users to find and book parking spaces in urban areas while offering additional features like ride-sharing, parking reservations, and a smooth user experience. The app leverages real-time parking management systems and integrates with payment gateways like Razorpay for parking and subscription payments.

This application also supports OAuth authentication for a secure login process via Google. It integrates Google Maps API for location-based services, helping users find and book parking spaces effortlessly.

**Features**
User Authentication: Secure sign-in via Google OAuth.
Parking Reservations: Users can search, book, and manage parking spaces (hourly, daily, monthly).
Ride-Sharing: Users can enter their location, get the fastest route, and calculate the cost based on various vehicle types (bike, car, shuttle, e-rickshaw).
Real-Time Parking System: Dynamic spot assignment and flexible cancellation with refund policies.
QR Code Generation: Digital pass for parking entry, making it easy for users to access their reserved parking spot.
Payment Integration: Supports parking and subscription payments through Razorpay (India-based).

**Tech Stack**

**Frontend:**
Android (Jetpack Compose): Native Android UI development.
Google Maps SDK: For location-based services and navigation.

**Backend:**
Spring Boot: For handling the API requests related to parking, ride-sharing, and user management.
Firebase Authentication: For secure authentication using OAuth.
Razorpay API: For integrating payments.

**Database:**
MySQL: Stores parking space data, user profiles, and ride history.
Firebase Firestore: Stores user-specific data like ride history, parking bookings, etc.


**How to Set Up the Project

1. Clone the Repository**
bash
Copy
Edit
git clone https://github.com/yourusername/park-ride-app.git

**2. Backend Setup**
Clone the Spring Boot repository (if separate) and import it into your IDE.
Configure the database connection (MySQL or another preferred RDBMS).
Set up Firebase Authentication and enable Google OAuth.
Install the necessary dependencies:

mvn clean install

Run the Spring Boot application:
bash
Copy
Edit
mvn spring-boot:run
**3. Android Application Setup**
Open the Android Project in Android Studio.
Install dependencies using Gradle:
bash
Copy
Edit
./gradlew build
Set up Google Maps API for the Android app by getting an API key from the Google Cloud Console and adding it to the google_maps_api.xml.
Configure Firebase Authentication for OAuth integration in your google-services.json.

**4. Razorpay Payment Integration**
Create a Razorpay account, get your API key, and integrate it into the backend for payment processing.

**Offline Functionality**
The application is designed to work offline by using SharedPreferences to store ride history and parking history locally when the internet is not available. The data is loaded from SharedPreferences and updated when an internet connection is available.

**Future Enhancements**
Push Notifications: For parking availability updates and booking confirmations.
Multi-Language Support: Allow users to interact with the app in their preferred language.
AI Integration: To predict parking spot availability and suggest optimal parking locations.

**Contributing**
We welcome contributions to the Park & Ride app! If you'd like to contribute:

Fork the repository.
Create a new branch (git checkout -b feature-name).
Make your changes and commit them (git commit -am 'Add new feature').
Push to the branch (git push origin feature-name).
Open a Pull Request.


**Contact**
If you have any questions, feel free to contact me via:

Email: sandeshpandey2032@gmail.com
GitHub: Sandesh032

Thanks for checking out the Park & Ride app! 🚗✨
