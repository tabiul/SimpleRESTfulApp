# A Simple RESTful Application

Assume a hypothetical scenario where you are a bird lover and you want to keep track of birds that you have sighted. This app provides a RESTful API to do the following

   * __Add Bird__ - add a new bird
   * __View Bird__ - list of birds
   * __Delete Bird__ - delete a bird
   * __Add Sighting__ - add a new sighting
   * __View Sighting__ - query sighting
   * __Quit__ - shutdown the app

## Technology

   * Jersey
   * Guice
   * Jetty
   * JAXB
   * Sqlite

## How to Build

### Windows

`gradlew.bat fatJar`

### Linux

`./gradlew fatJar`


## Options

    -data <folder path>    folder path containing data xml files (required)
    -port <number>         listening port (if not provided then set to 3000)
    -proc_count <number>   number of worker process (if not provided set to 2).
                           This defines the number of worker process in Jetty to process request

## How to run

`java -jar build\libs\app.jar --data <path to data folder>`

A sample data folder is available in the root folder

## API

### Add Bird

`/add/bird`

___Attributes___

   * name
   * color
   * height
   * weight

___Example___

`add/bird?name=dove&color=white&height=1.0&weight=1.0`


### View Bird

`/view/bird`

___Sample Output___

    [{"name":"test1","color":"red","weight":10.0,"height":1.0,"sightings":[{"location":"jurong","date":1309492800000},{"location":"bedok","date":1312171200000}]},{"name":"test2","color":"orange","weight":9.0,"height":2.0,"sightings":[{"location":"pasir ris","date":1309496400000},{"location":"tampines","date":1309579200000},{"location":"jurong","date":1322665200000}]},{"name":"dove","color":"white","weight":1.0,"height":1.0,"sightings":[]}]

### Delete Bird

`/delete/bird?name=<?>`

___Example___

`/delete/bird?name=dove`

### Add Sighting

`/add/sighting?name=<?>&location=<?>&date=<?>`

___Attributes___

   * name
   * location
   * date (dd/MM/yyyy HH:mm)

___Example___

`/add/sighting?name=dove&location=house&date=10/01/2013 21:00`

### View Sighting

`/view/sighting`

___Attributes___

   * name (regular expression)
   * fromDate (dd/MM/yyyy)
   * toDate  (dd/MM/yyyy)

___Example___

`/view/sighting?name=t.*&&fromDate=01/07/2010&toDate=01/07/2012`

___Sample Output___

    [{"name":"test2","color":"orange","weight":9.0,"height":2.0,"sightings":[{"location":"pasir ris","date":"01/07/2011 15:00"},{"location":"tampines","date":"02/07/2011 14:00"},{"location":"jurong","date":"01/12/2011 01:00"}]},{"name":"test1","color":"red","weight":10.0,"height":1.0,"sightings":[{"location":"jurong","date":"01/07/2011 14:00"},{"location":"bedok","date":"01/08/2011 14:00"}]}]


### Quit

This API must be called using the ___HTTP Delete___

`/quit`

This shutdowns the Application


## Data

### Bird

    <birds>
        <bird>
           <name>test1</name>
           <color>red</color>
           <height>1.0</height>
           <weight>10.0</weight>
        </bird
        <bird>
           <name>test2</name>
           <color>orange</color>
           <height>2.0</height>
           <weight>9.0</weight>
           </bird>
    </birds>

### Sighting

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <sightings>
        <sighting birdname="test1">
            <location>jurong</location>
            <date>01/07/2011 14:00</date><
        /sighting>
        <sighting birdname="test1">
            <location>bedok</location>
            <date>01/08/2011 14:00</date>
        </sighting>
    </sightings>

## Backup

A background thread runs prediocally to backup the Sqlite database into the xml files that was passed in as parameter

## TODO

   * Terrible error handling. Need to improve
   * Add logging framework so as to make it easy to debug

