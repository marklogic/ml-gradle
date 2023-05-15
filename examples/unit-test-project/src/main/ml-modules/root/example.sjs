function notCovered() {
	return "This function is not expected to be covered by the test suite."
}

function echo(value) {
	console.log("Just testing");
	return "Echo: " + value;
}

module.exports = {
	notCovered,
	echo
}
