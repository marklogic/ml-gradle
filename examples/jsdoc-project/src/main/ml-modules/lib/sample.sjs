
function internalHello(str) {
    return `Hello ${str}` 
}

/**
 * Sample Module
 * 
 * @module "/sample.sjs"
 * @exports hello
 */
module.exports = {
    /**
     * Returns "Hello " plus the str parameter
     * 
     * @example
     * const sampleLib = require("/sample.sjs")
     * // returns "Hello World"
     * sampleLib.hello("World")
     * 
     * @param {string} str the string to say hello to
     */
    hello: internalHello
}