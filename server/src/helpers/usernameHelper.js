export function formatUsername(fullName) {
    const namePart = fullName
        .replace(/\s+/g, '')
        .replace(/[^A-Za-z]/g, '')
        .toUpperCase()
        .substring(0, 12);

    const hexPart = Math.floor(Math.random() * 0x10000).toString(16).padStart(4, '0');

    return namePart + hexPart;
}
