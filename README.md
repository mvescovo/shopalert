# Shop Alert
An Android app to alert the user when an item of interest is nearby.
# Install
###### Front end
Android Studio was used to build the app and is recommended for the Android component.
###### Back end
The app requires a back end to work and this is not included.

- an appropriate remote SQL database,
- App Engine server or equivalent to parse and query data to and from the remote SQL server,
- a page scraping server to get prices from the chosen source.

The app also used Google Cloud Messaging and some Google API's for geofencing and signin.
# Features
- User can add a product to the database.
- The product is then searched via a pricing website to obtain the 3 cheapest prices.
- The price and location of the shop is sent back to the device.
- When the user comes close to the shop a notification alerts the user.
- Different user accounts can hold different products while using the same device (via Google signin).
- Products will follow the user between devices via the cloud.
# Limitations
- Currently it can only search one particular website which only finds prices for electronics items. Therefore other items won't return any shops.
- Obtaining product images was not part of the requirements and currently there is only a placeholder image of a pizza stone (which has nothing to do with electronics but conceivably a future version might be able to add this product).
- No function to edit or delete products from the app itself.
- The directions button on a shop doesn't do anything, but touching the placeholder image instead will give directions (via Google maps).
- Shops will show the relevant Google Street View image but not all shops have such an image and so will not show one and there's no placeholder for this situation. 
# Issues
- Any backend services that require payment have been shutdown since the assignment is over and the free educational use has expired. As such, significant work is required to get the app back up and running; you can't simply run the app and expect it to work.

# Licence
[![AUR](https://img.shields.io/aur/license/yaourt.svg)]()

[GNU General Public License v3.0](http://choosealicense.com/licenses/gpl-3.0/)