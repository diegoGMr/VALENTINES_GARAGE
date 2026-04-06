function formatUsername(fullName) {
    // Remove spaces and keep only letters, converts to uppercase, max 12 chars
    const namePart = fullName
        .replace(/\s+/g, '')
        .replace(/[^A-Za-z]/g, '')
        .toUpperCase()
        .substring(0, 12);

    // Generates 4 random hexadecimal characters
    const hexPart = Math.floor(Math.random() * 0x10000).toString(16).padStart(4, '0');

    return namePart + hexPart;
}

module.exports = { formatUsername };