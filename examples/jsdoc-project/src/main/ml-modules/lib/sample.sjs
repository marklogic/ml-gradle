/**
 * Sample Module
 * 
 * @module "/sample.sjs"
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
    hello: function(str) {
        return `Hello ${str}` 
    }
}