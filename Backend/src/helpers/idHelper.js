function generateRandomHex(charCount) {
    if (!charCount || charCount <= 0) {
        charCount = 6; // Default to 6 characters if not specified or invalid
    }
    const validChars = '0123456789ABCDEFGHI';
    let result = '';
    for (let i = 0; i < charCount; i++) {
        result += validChars.charAt(Math.floor(Math.random() * validChars.length));
    }
    return result;
}

module.exports = generateRandomHex;