function submitReview(eventId) {
    const form = event.target.closest('form');
    const nameInput = form.querySelector('[name="name"]');
    const ratingInput = form.querySelector('[name="rating"]');
    const commentInput = form.querySelector('[name="comment"]');

    // Create review object
    const review = {
        userName: nameInput.value,
        rating: parseInt(ratingInput.value),
        comment: commentInput.value,
        type: eventId === 'general-customer-review' ? 'platform' : 'event',
        eventId: eventId === 'general-customer-review' ? null : eventId
    };

    // Send to backend
    fetch('/api/reviews', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(review)
    })
        .then(response => response.json())
        .then(data => {
            alert('Review submitted successfully!');
            form.reset();
            // Hide the form
            const commentSection = form.closest('.event-comment-section, #comment-section');
            if (commentSection) {
                commentSection.classList.add('hidden');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error submitting review. Please try again.');
        });

    // Prevent form submission
    return false;
}

function openEditForm(reviewId) {
    // Fetch review data
    fetch(`/api/reviews`)
        .then(response => response.json())
        .then(reviews => {
            const review = reviews.find(r => r.id === reviewId);
            if (review) {
                document.querySelector('#editReviewId').value = review.id;
                document.querySelector('#editReviewName').value = review.userName;
                document.querySelector('#editReviewRating').value = review.rating;
                document.querySelector('#editReviewComment').value = review.comment;
                document.querySelector('#edit-review-form').style.display = 'block';
            }
        });
}

function submitEditReview() {
    const reviewId = document.querySelector('#editReviewId').value;
    const review = {
        userName: document.querySelector('#editReviewName').value,
        rating: parseInt(document.querySelector('#editReviewRating').value),
        comment: document.querySelector('#editReviewComment').value,
        // We need to fetch the original review to get these values
        type: 'platform', // This will be overwritten with the correct value
        eventId: null     // This will be overwritten with the correct value
    };

    // First get the original review to preserve type and eventId
    fetch(`/api/reviews`)
        .then(response => response.json())
        .then(reviews => {
            const originalReview = reviews.find(r => r.id === reviewId);
            if (originalReview) {
                review.type = originalReview.type;
                review.eventId = originalReview.eventId;

                // Now update the review
                fetch(`/api/reviews/${reviewId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(review)
                })
                    .then(response => response.json())
                    .then(data => {
                        alert('Review updated successfully!');
                        document.querySelector('#edit-review-form').style.display = 'none';
                        // Update the UI if needed
                        if (review.type === 'platform') {
                            // Update platform review display
                            const platformReview = reviews.find(r => r.id === reviewId && r.type === 'platform');
                            if (platformReview) {
                                document.getElementById('review-comment').textContent = `"${data.comment}"`;
                                document.getElementById('review-name').textContent = data.userName;
                            }
                        } else {
                            // Update event review display
                            // This would need to find the specific event review section
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Error updating review.');
                    });
            }
        });
}

function cancelEdit() {
    document.querySelector('#edit-review-form').style.display = 'none';
}