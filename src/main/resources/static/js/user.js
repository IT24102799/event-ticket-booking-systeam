// User Service
class UserService {
    constructor() {
        this.apiUrl = '/api/users';
    }

    // Check if user is logged in
    isLoggedIn() {
        return localStorage.getItem('user') !== null;
    }

    // Get current user from localStorage
    getCurrentUser() {
        const userJson = localStorage.getItem('user');
        if (!userJson) return null;
        
        try {
            return JSON.parse(userJson);
        } catch (error) {
            console.error('Error parsing user data:', error);
            localStorage.removeItem('user');
            return null;
        }
    }

    // Update auth UI based on login status
    updateAuthUI() {
        const isLoggedIn = this.isLoggedIn();
        const currentUser = this.getCurrentUser();
        
        console.log('Updating UI based on login status:', isLoggedIn);
        
        // Update header elements
        document.querySelectorAll('.auth-link').forEach(el => {
            el.style.display = isLoggedIn ? 'none' : '';
        });
        
        document.querySelectorAll('.user-profile-menu, .mobile-user-menu').forEach(el => {
            el.style.display = isLoggedIn ? '' : 'none';
        });
        
        // Update username if available
        if (isLoggedIn && currentUser) {
            document.querySelectorAll('#header-username, #mobile-username').forEach(el => {
                el.textContent = currentUser.name || 'User';
            });
        }
    }

    // Logout user
    logout() {
        localStorage.removeItem('user');
        window.location.href = '/';
    }

    // Delete user account
    async deleteUser(userId) {
        try {
            const response = await fetch(`${this.apiUrl}/${userId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || 'Failed to delete account');
            }
            
            return true;
        } catch (error) {
            console.error('Error deleting user account:', error);
            throw error;
        }
    }

    // Update user profile
    async updateUser(userId, userData) {
        try {
            const response = await fetch(`${this.apiUrl}/${userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || 'Failed to update profile');
            }
            
            return await response.json();
        } catch (error) {
            console.error('Error updating user profile:', error);
            throw error;
        }
    }

    // Login user
    async login(email, password) {
        try {
            console.log('Attempting login with:', email);
            const response = await fetch(`${this.apiUrl}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || 'Login failed');
            }
            
            const userData = await response.json();
            console.log('Login successful, user data:', userData);
            
            // Make sure we have all required fields
            if (!userData.name) {
                console.warn('User name is missing from response');
            }
            
            localStorage.setItem('user', JSON.stringify(userData));
            return userData;
        } catch (error) {
            console.error('Error during login:', error);
            throw error;
        }
    }
    
    // Register new user
    async register(userData) {
        try {
            console.log('Registering new user:', userData.email);
            const response = await fetch(this.apiUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || 'Registration failed');
            }
            
            const user = await response.json();
            console.log('Registration successful, user data:', user);
            
            localStorage.setItem('user', JSON.stringify(user));
            return user;
        } catch (error) {
            console.error('Error during registration:', error);
            throw error;
        }
    }
}

// Initialize user service
window.userService = new UserService();

// Initialize user interface based on authentication state
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing user service');
    const userService = window.userService;
    
    // Update UI based on authentication state
    userService.updateAuthUI();
    
    // Handle logout
    document.querySelectorAll('#header-logout, .logout-link').forEach(el => {
        el.addEventListener('click', function(e) {
            e.preventDefault();
            userService.logout();
        });
    });
    
    // Toggle user dropdown menu
    const userMenuButton = document.getElementById('user-menu-button');
    const userDropdown = document.getElementById('user-dropdown');
    
    if (userMenuButton && userDropdown) {
        userMenuButton.addEventListener('click', function() {
            userDropdown.classList.toggle('hidden');
        });
        
        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!userMenuButton.contains(e.target) && !userDropdown.contains(e.target)) {
                userDropdown.classList.add('hidden');
            }
        });
    }
    
    // Handle login form submission
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const email = document.getElementById('login-email').value;
            const password = document.getElementById('login-password').value;
            
            // Simple validation
            if (!email || !password) {
                document.getElementById('password-error').classList.remove('hidden');
                return;
            }
            
            try {
                console.log('Attempting to login with:', email);
                // Use the userService for login
                const userData = await window.userService.login(email, password);
                console.log('Login successful, redirecting to profile page');
                
                // Redirect to user profile
                window.location.href = '/user-profile';
            } catch (error) {
                console.error('Error during login:', error);
                alert('Login failed. Please check your credentials and try again.');
            }
        });
    }
    
    // Handle register form submission
    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const name = document.getElementById('register-name').value;
            const email = document.getElementById('register-email').value;
            const phone = document.getElementById('register-phone').value;
            const password = document.getElementById('register-password').value;
            const confirmPassword = document.getElementById('register-confirm-password').value;
            
            // Simple validation
            if (!name || !email || !password) {
                alert('Name, email and password are required');
                return;
            }
            
            if (password !== confirmPassword) {
                document.getElementById('password-match-error').classList.remove('hidden');
                return;
            } else {
                document.getElementById('password-match-error').classList.add('hidden');
            }
            
            try {
                // Use the userService for registration
                const userData = await window.userService.register({
                    name: name,
                    email: email,
                    phone: phone,
                    password: password
                });
                
                console.log('Registration successful, redirecting to profile page');
                
                // Redirect to user profile
                window.location.href = '/user-profile';
            } catch (error) {
                console.error('Error during registration:', error);
                
                if (error.message.includes('Email already in use')) {
                    document.getElementById('email-error').classList.remove('hidden');
                } else {
                    alert('Registration failed: ' + error.message);
                }
            }
        });
    }
});