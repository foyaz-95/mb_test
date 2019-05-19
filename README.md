# The Brief:

Create a mini version of the Moneybox app that will allow existing users to login, check their account and add money to their moneybox.

## Part A Fixes
1. layout of screen: used constraints to align correctly
2. error messages not correctly dissapearing - changed logic/ordering of error statements
3. full name regex was incorrect - fixed - from online
4. email regex incorrect - fixed - from online
5. used while loop and frame numbers to loop to task requirements

## Part B - Added 2 new screens
1. added a "home area" that the user is shown upon logging in (bearer token is also passed after being extracted from JSON message)
2. added textviews to display different account information that is extracted from a JSON message received from the server (after sending request to GET /investorproducts)
3. made textviews clickable to allow a user to go to the "add money" screen, user can add money by clicking on "Add £10" button from this screen

### TODO
1. separate calls to server (create own class and methods etc...)
2. write tests to assert whether values are correct (create test user, assign products and plan values etc...) instead of printlines
3. implement dynamic card creation to display account information instead of hardcoding