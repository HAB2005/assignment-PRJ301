package controller;

import dao.UserDAO;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class EditUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String userId = req.getParameter("userId");
        System.out.println(userId);

        //Trường hợp 1: Chọn người để sửa (chỉ có userId, không có action)
        if (action == null || !action.equals("updateUser")) {
            UserDAO userDAO = new UserDAO();
            User u = userDAO.getUserById(Integer.parseInt(userId));

            if (u != null) {
                req.setAttribute("userId", u.getUserId());
                req.setAttribute("username", u.getUsername());
                req.setAttribute("password", u.getPassword());
                req.setAttribute("email", u.getEmail());
                req.setAttribute("fullname", u.getFullName());
                req.setAttribute("mode", "edit");
            } else {
                req.setAttribute("error", "Không tìm thấy người dùng.");
            }

            req.getRequestDispatcher("/common/userForm.jsp").forward(req, resp);
            return;
        }

        //Trường hợp 2: Submit cập nhật thông tin
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String fullname = req.getParameter("fullname");

        UserDAO userDAO = new UserDAO();
        boolean updated = userDAO.updateUser(Integer.parseInt(userId), username, password, email, fullname);

        req.setAttribute("userId", userId);
        req.setAttribute("username", username);
        req.setAttribute("password", password);
        req.setAttribute("email", email);
        req.setAttribute("fullname", fullname);
        req.setAttribute("mode", "edit");

        if (updated) {
            req.setAttribute("message", "Cập nhật thành công!");
        } else {
            req.setAttribute("error", "Cập nhật thất bại. Vui lòng thử lại.");
        }

        req.getRequestDispatcher("/common/userForm.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDAO dao = new UserDAO();
        List<User> userList = dao.getAllUsers();
        req.setAttribute("userList", userList);
        req.setAttribute("formAction", "edit_user");
        req.getRequestDispatcher("/common/allUser.jsp").forward(req, resp);
    }
}
