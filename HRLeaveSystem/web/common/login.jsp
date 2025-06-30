<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
    // Danh sách username và map username-password
    java.util.List<String> rememberedUsernames = new java.util.ArrayList<>();
    java.util.Map<String, String> userPassMap = new java.util.HashMap<>();

    String rememberedUsername = "";
    String rememberedPassword = "";

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().startsWith("rememberedUsername_")) {
                String uname = cookie.getName().substring("rememberedUsername_".length());
                String pwd = cookie.getValue();
                rememberedUsernames.add(uname);
                userPassMap.put(uname, pwd);

                // nếu là lần đầu chưa nhập gì thì gán cái đầu tiên làm mặc định
                if (rememberedUsername.isEmpty()) {
                    rememberedUsername = uname;
                    rememberedPassword = pwd;
                }
            }
        }
    }
%>


<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Login - Leave Request System</title>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gradient-to-r from-indigo-200 via-purple-200 to-pink-200 min-h-screen flex items-center justify-center font-sans">
        <div class="bg-white bg-opacity-90 shadow-2xl rounded-3xl p-10 max-w-md w-full backdrop-blur">
            <div class="text-center mb-8">
                <h1 class="text-3xl font-bold text-purple-800">Leave Request System</h1>
                <p class="text-gray-600 mt-2">Please log in to continue</p>
            </div>

            <!-- Hiển thị thông báo lỗi nếu có -->
            <c:if test="${not empty error}">
                <div class="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded-xl text-sm">
                    ${error}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login-handler" method="post" class="space-y-6">
                <div class="relative">
                    <label for="username" class="block text-sm font-medium text-gray-700">Username</label>
                    <input type="text" id="username" name="username" required
                           placeholder="Enter your username"
                           onfocus="showSuggestions()"
                           oninput="filterSuggestions()"
                           class="mt-1 w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-400">


                    <ul id="suggestionBox" class="hidden absolute z-10 mt-1 w-full bg-white border border-gray-300 rounded-xl shadow-lg max-h-48 overflow-auto">
                        <% for (String uname : rememberedUsernames) { %>
                        <li onclick="selectSuggestion('<%= uname %>', '<%= userPassMap.get(uname) %>')"
                            class="px-4 py-2 hover:bg-purple-100 cursor-pointer text-sm">
                            <%= uname %>
                        </li>
                        <% } %>
                    </ul>
                </div>



                <div>
                    <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
                    <input type="password" id="password" name="password" required
                           placeholder="Enter your password"
                           class="mt-1 w-full px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-400">


                </div>

                <div class="flex items-center justify-between text-sm">
                    <label class="flex items-center">
                        <input type="checkbox" name="remember" class="form-checkbox rounded text-purple-600">
                        <span class="ml-2 text-gray-700">Remember me</span>
                    </label>
                    <a href="#" class="text-purple-600 hover:underline">Forgot your password ?</a>
                </div>

                <button type="submit"
                        class="w-full bg-purple-600 hover:bg-purple-700 text-white font-semibold py-3 rounded-xl shadow-md transition duration-300">
                    Log In
                </button>
            </form>
        </div>
        <script>
            const suggestionBox = document.getElementById('suggestionBox');
            const usernameInput = document.getElementById('username');
            const passwordInput = document.getElementById('password');

            function showSuggestions() {
                if (suggestionBox)
                    suggestionBox.classList.remove('hidden');
            }

            function selectSuggestion(username, password) {
                usernameInput.value = username;
                passwordInput.value = password;
                suggestionBox.classList.add('hidden');
            }

            function filterSuggestions() {
                const filter = usernameInput.value.toLowerCase();
                const items = suggestionBox.querySelectorAll('li');
                items.forEach(item => {
                    item.style.display = item.textContent.toLowerCase().includes(filter) ? '' : 'none';
                });
            }

            document.addEventListener('click', function (e) {
                if (!suggestionBox.contains(e.target) && e.target !== usernameInput) {
                    suggestionBox.classList.add('hidden');
                }
            });
        </script>
    </body>
</html>
