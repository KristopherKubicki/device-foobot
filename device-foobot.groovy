/**
 *  Foobot
 *    This is a very basic sensor device type for the Foobot air quality sensor
 *
 *  Special thanks for Jason Mok on this one
 *
 */
 preferences {
    input("username", "text", title: "Username", description: "Your Foobot username (usually an email address)")
    input("password", "password", title: "Password", description: "Your Foobot password")
    input("serial", "serial", title: "Key", description: "Your 16-digit Foobot Serial")
}
 
metadata {
	definition (name: "Foobot", namespace: "KristopherKubicki", author: "Kristopher Kubicki") {
		capability "Polling"
        capability "Refresh"
        capability "Sensor"
		capability "relativeHumidityMeasurement"
        capability "temperatureMeasurement"
     
     	attribute "pollution", "number"
        attribute "co2", "number"
        attribute "particle", "number"
        attribute "voc", "number"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {   

        valueTile("pollution", "device.pollution", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue}% GPI', unit:"%"
        }
        valueTile("co2", "device.co2", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue} CO2 ppm', unit:"ppm"
        }
        valueTile("voc", "device.voc", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue} VOC ppb', unit:"ppb"
        }
        valueTile("particle", "device.particle", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue} µg/m³', unit:"µg/m³ PM2.5"
        }
        valueTile("humidity", "device.humidity", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue}% humidty', unit:"%"
        }
        valueTile("temperature", "device.temperature", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue}°', unit:"°"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main "pollution"
        details(["pollution","co2","voc","particle","humidity", "temperature", "refresh"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def refresh() { 
	poll()
}

// handle commands
def poll() {
    
    def start = new Date(Calendar.instance.time.time-1800000).format("yyyy-MM-dd'T'HH:MM':00'");
    def stop = new Date(Calendar.instance.time.time+1800000).format("yyyy-MM-dd'T'HH:MM':00'");
       
	def params = [
        uri:  'https://api.foobot.io',
        path: "/v2/device/${settings.serial}/datapoint/$start/$stop/0/",
        headers: [Authorization : getApiAuth()] ,
        contentType: 'application/json'
    ]
    try {
        httpGet(params) {resp ->
            log.debug "resp data: ${resp.data}"
            log.debug "pm: ${resp.data.datapoints[-1][1]}"
            sendEvent(name: "particle", value: resp.data.datapoints[-1][1].round(1), unit: "µg/m³ PM2.5")
            log.debug "tmp: ${resp.data.datapoints[-1][2]}"
            def tmp = resp.data.datapoints[-1][2]
            if(getTemperatureScale() == "C") {
            	tmp = Integer.tmp
                sendEvent(name: "temperature", value: tmp, unit: "°C")
            }
            else {
            	tmp = celsiusToFahrenheit(tmp) as Integer
                sendEvent(name: "temperature", value: tmp, unit: "°F")
            }
            log.debug "hum: ${resp.data.datapoints[-1][3]}"
            sendEvent(name: "humidity", value: resp.data.datapoints[-1][3] as Integer, unit: "%")
            log.debug "co2: ${resp.data.datapoints[-1][4]}"
            sendEvent(name: "co2", value: resp.data.datapoints[-1][4] as Integer, unit: "ppm")
            log.debug "voc: ${resp.data.datapoints[-1][5]}"
            sendEvent(name: "voc", value: resp.data.datapoints[-1][5] as Integer, unit: "ppb")
            log.debug "allpollu: ${resp.data.datapoints[-1][6]}"
            sendEvent(name: "pollution", value: resp.data.datapoints[-1][6] as Integer, unit: "%")
        }
    } catch (e) {
        log.error "error: $e"
    }
    log.debug "success"
}

def getApiAuth() {
    def basicAuth = "${settings.username}:${settings.password}".encodeAsBase64()
    log.debug( "Using token $basicAuth" )	
    return "Basic " + basicAuth
}
