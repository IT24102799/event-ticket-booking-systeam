document.addEventListener('DOMContentLoaded', function() {
    // Connect to websocket endpoint
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    // Connect to WebSocket
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        // Subscribe to /topic/reviews for updates
        stompClient.subscribe('/topic/reviews', function(message) {
            const review = JSON.parse(message.body);
            console.log('Received review update:', review);
            updateReviewList(review);
        });
    }, function(error) {
        console.error('WebSocket Error:', error);
    });

    // Function to update review list
    function updateReviewList(review) {
        // Handle string message for deletions
        if (typeof review === 'string' && review.startsWith('Review deleted:')) {
            const id = review.split(':')[1].trim();
            document.querySelectorAll('.review-item').forEach(item => {
                if (item.getAttribute('data-id') === id) {
                    item.remove();
                }
            });
            return;
        }

        const eventContainer = document.getElementById('event-reviews-container');
        const platformContainer = document.getElementById('platform-reviews-container');

        // Check if review already exists (for updates)
        const existingReview = document.querySelector(`.review-item[data-id="${review.id}"]`);
        if (existingReview) {
            existingReview.innerHTML = `
                <p><strong>${review.userName}</strong>: ${review.comment} (Rating: ${review.rating}/5)</p>
                <p class="text-sm text-gray-500">Date: ${review.date}</p>
                <div class="mt-2">
                    <button onclick="editReview('${review.id}')" class="bg-blue-500 text-white px-2 py-1 rounded mr-2">Edit</button>
                    <button onclick="deleteReview('${review.id}')" class="bg-red-500 text-white px-2 py-1 rounded">Delete</button>
                </div>
            `;
            return;
        }

        // Create review element for new reviews
        const reviewElement = document.createElement('div');
        reviewElement.className = 'review-item p-4 bg-white rounded shadow mb-4';
        reviewElement.setAttribute('data-id', review.id);
        reviewElement.innerHTML = `
            <p><strong>${review.userName}</strong>: ${review.comment} (Rating: ${review.rating}/5)</p>
            <p class="text-sm text-gray-500">Date: ${review.date}</p>
            <div class="mt-2">
                <button onclick="editReview('${review.id}')" class="bg-blue-500 text-white px-2 py-1 rounded mr-2">Edit</button>
                <button onclick="deleteReview('${review.id}')" class="bg-red-500 text-white px-2 py-1 rounded">Delete</button>
            </div>
        `;

        // Append to appropriate container
        if (review.type === 'event') {
            eventContainer.appendChild(reviewElement);
        } else {
            platformContainer.appendChild(reviewElement);
        }
    }

    // Fetch initial reviews
    fetch('/api/reviews')
        .then(response => response.json())
        .then(reviews => {
            reviews.forEach(updateReviewList);
        })
        .catch(error => console.error('Error fetching reviews:', error));

    // Edit review function
    window.editReview = function(id) {
        fetch(`/api/reviews/${id}`)
            .then(response => response.json())
            .then(review => {
                const userName = prompt('Enter new name:', review.userName);
                const rating = prompt('Enter new rating (1-5):', review.rating);
                const comment = prompt('Enter new comment:', review.comment);

                if (userName && rating && comment) {
                    const updatedReview = {
                        ...review,
                        userName: userName,
                        rating: parseInt(rating),
                        comment: comment
                    };

                    fetch(`/api/reviews/${id}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(updatedReview)
                    })
                        .then(response => response.json())
                        .then(data => {
                            console.log('Review updated:', data);
                        })
                        .catch(error => console.error('Error updating review:', error));
                }
            })
            .catch(error => console.error('Error fetching review:', error));
    };

    // Delete review function
    window.deleteReview = function(id) {
        if (confirm('Are you sure you want to delete this review?')) {
            fetch(`/api/reviews/${id}`, {
                method: 'DELETE'
            })
                .then(() => {
                    console.log('Review deleted:', id);
                })
                .catch(error => console.error('Error deleting review:', error));
        }
    };
});