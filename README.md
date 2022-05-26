# Recipe Sharing Application

### Steps to run the application

1. Install mongoDb and get it up and running.
2. Clone the repository and import it as a gradle project.
3. Get the connection string to the mongoDb and add it to application environment variable with the key **CONNECTION_STRING**.
4. Run the application and use the postman collection **assignment_recipe_sharing.postman_collection.json**  to make API calls to the application.
5. Steps to sign up and make API calls from postman collection:
   1. Import the collection to postman
   2. Inside the security folder make a call to /user/add with user details in the body and use the **id** returned in the next steps.
   3. Copy the userId from the previous step and paste to the path variable in the /user/{userId}/update endpoint. Provide **username** and **password** in the request body and make the call.
   4. Use the same **username** and **password** in /authenticate endpoint and make the call.
   5. Store the **bearerToken** returned from the previous step in the Authorization header for all API endpoints under **recipe** folder. 
6. Refer to the documentation **Recipe-sharing-documentation.pdf** for more details. 