window.onload = init;
var socket = new WebSocket("ws://localhost:8080/WebsocketHome/actions");
socket.onmessage = onMessage;

function onMessage(event) {
    var hospital = JSON.parse(event.data);
    if (hospital.action === "add") {
        printDeviceElement(hospital);
    }
    
    if (hospital.action === "remove") {
        document.getElementById(hospital.id).remove();
        //device.parentNode.removeChild(device);
    }
    
    if (hospital.action === "nuevaCama" || hospital.action === "cambiarEstadoCama" ||  hospital.action === "eliminarCama") {
        var node = document.getElementById(hospital.id);
        var statusText = node.children[1];
        statusText.innerHTML = hospital.camas + "<br>";
    }

    /*
     if (device.action === "toggle") {
     var node = document.getElementById(device.id);
     var statusText = node.children[2];
     if (device.status === "On") {
     statusText.innerHTML = "Status: " + device.status + " (<a href=\"#\" OnClick=toggleDevice(" + device.id + ")>Turn off</a>)";
     } else if (device.status === "Off") {
     statusText.innerHTML = "Status: " + device.status + " (<a href=\"#\" OnClick=toggleDevice(" + device.id + ")>Turn on</a>)";
     }
     }
     */
}

function addCama(element) {
    var id = element;
    var DeviceAction = {
        action: "nuevaCama",
        id: id
    };
    socket.send(JSON.stringify(DeviceAction));
}

function cambiarEstadoCama(idH, idC) {
    var idHospital = idH;
    var idCama = idC;
    var DeviceAction = {
        action: "cambiarEstadoCama",
        idHospital: idHospital,
        idCama: idCama
    };
    socket.send(JSON.stringify(DeviceAction));
}

function eliminarCama(idH, idC) {
    var idHospital = idH;
    var idCama = idC;
    var DeviceAction = {
        action: "eliminarCama",
        idHospital: idHospital,
        idCama: idCama
    };
    socket.send(JSON.stringify(DeviceAction));
}


function addDevice() {
    var DeviceAction = {
        action: "add"
    };
    socket.send(JSON.stringify(DeviceAction));
}

function removeDevice(element) {
    var id = element;
    var DeviceAction = {
        action: "remove",
        id: id
    };
    socket.send(JSON.stringify(DeviceAction));
}

/*function toggleDevice(element) {
 var id = element;
 var DeviceAction = {
 action: "toggle",
 id: id
 };
 socket.send(JSON.stringify(DeviceAction));
 }*/

function printDeviceElement(hospital) {
    var content = document.getElementById("content");

    var deviceDiv = document.createElement("div");
    deviceDiv.setAttribute("id", hospital.id);
    deviceDiv.setAttribute("class", "device Lights");
    content.appendChild(deviceDiv);

    var deviceName = document.createElement("span");
    deviceName.setAttribute("class", "deviceName");
    deviceName.innerHTML = "Hospital " + hospital.id + " <br> ";
    deviceDiv.appendChild(deviceName);
    
    var deviceName = document.createElement("span");
    deviceName.setAttribute("class", "deviceName");
    deviceName.innerHTML = "Camas: sin camas"  + " <br> ";
    deviceDiv.appendChild(deviceName);
    var Cama = document.createElement("span");
    Cama.setAttribute("class", "removeDevice");
    
    Cama.innerHTML = "<br><a href=\"#\" OnClick=addCama(" + hospital.id +  ")>Agregado de camas</a>";
    deviceDiv.appendChild(Cama);

    var removeDevice = document.createElement("span");
    removeDevice.setAttribute("class", "removeDevice");
    removeDevice.innerHTML = "<br><a href=\"#\" OnClick=removeDevice(" + hospital.id + ")>Eliminar Hospital</a>";
    deviceDiv.appendChild(removeDevice);
}

function showForm() {
    document.getElementById("addDeviceForm").style.display = '';
}

function hideForm() {
    document.getElementById("addDeviceForm").style.display = "none";
}

function formSubmit() {
    hideForm();
    document.getElementById("addDeviceForm").reset();
    addDevice();
}

function init() {
    hideForm();
}

              
