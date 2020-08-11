### **Start bot**
1. Specify bot url (**ngrok** can be used for tunneling local ports to public url) in callback settings of your community
2. Specify confirmation code and etc. in **application.properties**
3. Launch spring boot app with command from the classpath
    ```
    mvn spring-boot:run
    ```
4. Send a confirmation code from callback settings page
5. Enjoy
 
### Properties
| Property | Description (...?) |
|----------|-------------|
| api.token| VK access token |
| api.group.id | VK group ID |
| api.server.id | VK Callback Server ID |
| api.confirmationCode | Confirmation code |
| bot.server.port | HTTP port to listen |
| quote.prefix | Prefix before your message in quote |
