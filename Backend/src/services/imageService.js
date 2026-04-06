const fs = require('fs');
const path = require('path');
// this service is a simple implementation for handling image storage on the local filesystem. 
// It provides functions to save, delete, and update images for user profiles and trucks. Note that
// this service might not be the best for concurrent operations and definitely not be suitable for production.

//paths to directories for storing images
const USER_IMAGES_DIR = path.join(__dirname, '../../database/Images/UserProfileImages');
const TRUCK_IMAGES_DIR = path.join(__dirname, '../../database/Images/TruckImages');

//create directories if they don't exist
if (!fs.existsSync(USER_IMAGES_DIR)) fs.mkdirSync(USER_IMAGES_DIR, { recursive: true });
if (!fs.existsSync(TRUCK_IMAGES_DIR)) fs.mkdirSync(TRUCK_IMAGES_DIR, { recursive: true });

// Save image to user profile images
function saveUserImage(imageBuffer, imageName) {
	const filePath = path.join(USER_IMAGES_DIR, imageName);
	fs.writeFileSync(filePath, imageBuffer);
	return filePath;
}

// Save image to truck images
function saveTruckImage(imageBuffer, imageName) {
	const filePath = path.join(TRUCK_IMAGES_DIR, imageName);
	fs.writeFileSync(filePath, imageBuffer);
	return filePath;
}

// Delete user image
function deleteUserImage(imageName) {
	const filePath = path.join(USER_IMAGES_DIR, imageName);
	if (fs.existsSync(filePath)) fs.unlinkSync(filePath);
}

// Delete truck image
function deleteTruckImage(imageName) {
	const filePath = path.join(TRUCK_IMAGES_DIR, imageName);
	if (fs.existsSync(filePath)) fs.unlinkSync(filePath);
}

// Update user image
function updateUserImage(imageBuffer, imageName) {
	deleteUserImage(imageName);
	return saveUserImage(imageBuffer, imageName);
}

// Update truck image
function updateTruckImage(imageBuffer, imageName) {
	deleteTruckImage(imageName);
	return saveTruckImage(imageBuffer, imageName);
}

module.exports = {
	saveUserImage,
	saveTruckImage,
	deleteUserImage,
	deleteTruckImage,
	updateUserImage,
	updateTruckImage
};
