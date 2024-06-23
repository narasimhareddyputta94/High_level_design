package Stateful_Serve;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class UserController {

    @PostMapping("/login")
    public String login(@RequestParam String username, HttpSession session) {
        // In a real application, you would validate the username and password.
        session.setAttribute("username", username);
        return "User logged in";
    }

    @GetMapping("/profile")
    public String getProfile(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "No user is logged in";
        }
        return "Profile of " + username;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "User logged out";
    }
}
