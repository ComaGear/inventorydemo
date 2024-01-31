// @ts-nocheck
const currentUrl = window.location.host;

async function updateMeasure(){
    // TODO handle job about post a new measurement check <option> 'add Meas' is valid to post.
    // or update some measuremnt with put
}

async function productCreate(){

    let product_id = document.querySelector("#product_id")?.value;
    let product_name = document.querySelector("#product_name")?.value;
    let product_default_relative_uom = document.querySelector("#product_default_relative_uom")?.value;
    let active = document.querySelector("#active_yes")?.checked;

    let measurement_list_object = [];

    if(document.querySelector("#measurement_list").hasChildNodes){
        let list = document.querySelector("#measurement_list");
        let measures = list.getElementsByClassName("measure");
        console.log(measures);
        for(let i = 0; i < measures.length; i++){
            let measure = {};
            measure.product_id = product_id;
            measure.relative_id = measures[i].querySelector(".relative_id").innerHTML;
            measure.uom_name = measures[i].querySelector(".measure_uom_name").innerHTML;
            measure.measurement = measures[i].querySelector(".measure_size").innerHTML;
            measure.barcode = measures[i].querySelector(".barcode").innerHTML;

            measurement_list_object.push(measure);
        }
    }

    let jsonObject = {
        id: product_id,
        name : product_name,
        default_uom: product_default_relative_uom,
        activity: active,
        measurements: measurement_list_object
    };

    const boby = JSON.stringify(jsonObject);


    const url = "http://" + currentUrl + "/api/product/";
    const response = await fetch(url, {
        method : "POST",
        body: boby,
        headers : {
            "Content-Type": "application/json"
        },
    });

    response.json().then((data) =>{
        console.log(boby)
        console.log(data)
        if(data.id == product_id) updateMeasure(); // update only product succesful create in database.
    });
}

let createButton = document.querySelector("button#create");
createButton?.addEventListener("click", productCreate);

let to_edit_measurement = null;
let to_edit_element = null;

let measurement_is_creating = false;

// setup dialog click event;
function setupDialogSetting(){

    let dialog = document.querySelector("#measure_edit_dialog");

    let edit_measure_relative_id = document.querySelector("#edit_measure_relative_id");
    let edit_dialog_uom_name = document.querySelector("#edit_dialog_uom_name");
    let edit_dialog_measure_size = document.querySelector("#edit_dialog_measure_size");
    let edit_dialog_barcode = document.querySelector("#edit_dialog_barcode");

    let measure_dialog_close = document.querySelector("#measure_dialog_close");
    measure_dialog_close.addEventListener("click", (e)=>{
        dialog.close();
    });

    dialog.addEventListener("close", (e)=>{
        edit_dialog_uom_name.disabled = false;
        edit_measure_relative_id.disabled = false;
        measurement_is_creating = false;

        if(to_edit_element.querySelector(".measure_uom_name").innerHTML == "" 
            || to_edit_element.querySelector(".measure_size").innerHTML == "") {
            document.querySelector("#measurement_list").removeChild(to_edit_element);
        }
    });

    let measure_dialog_save = document.querySelector("#measure_dialog_save");
    measure_dialog_save.addEventListener("click", (e)=>{

        let old_relative_id = to_edit_element.querySelector(".relative_id").innerHTML;

        to_edit_element.id = `measure_${edit_measure_relative_id.value}`;

        to_edit_element.querySelector(".relative_id").innerHTML = edit_measure_relative_id.value;
        to_edit_element.querySelector(".measure_uom_name").innerHTML = edit_dialog_uom_name.value;
        to_edit_element.querySelector(".measure_size").innerHTML = edit_dialog_measure_size.value;
        to_edit_element.querySelector(".barcode").innerHTML = edit_dialog_barcode.value;

        if(to_edit_element.querySelector(".measure_uom_name").innerHTML == "" 
        || to_edit_element.querySelector(".measure_size").innerHTML == "") {
            alert("measure UOM or Size can't not be blank");
            edit_dialog_measure_size.required = true;
            edit_dialog_uom_name.required = true;
            return;
        }

        if(edit_dialog_measure_size.value < 0) {
            alert("measurement size should greater than zero");
            return;
        }

        // operation of update measure !! change to create product updating measure_list

        // update new measurement to default uom select option.
        
        if(measurement_is_creating) {
            insertNewDefaultUomOption(edit_measure_relative_id.value);
        } else {
            updateDefaultUomOption(old_relative_id, edit_measure_relative_id.value);
        }
        dialog.close();
    });
    edit_dialog_uom_name.addEventListener("change", (e)=>{
        edit_measure_relative_id.value = `${product_id.value}-${edit_dialog_uom_name.value}`;
        edit_measure_relative_id.disabled=true;
    });
    edit_measure_relative_id.addEventListener("change", (e)=>{
        edit_dialog_uom_name.disabled=true;
    });
}

setupDialogSetting();

function openDialog(measure_element){

    let dialog = document.querySelector("#measure_edit_dialog");
    dialog.showModal();

    to_edit_element = {};
    to_edit_element = measure_element;
    to_edit_measurement = {};
    to_edit_measurement.product_id = document.querySelector("#product_id").value;
    to_edit_measurement.relative_id = to_edit_element.querySelector(".relative_id").innerHTML;
    to_edit_measurement.measure_uom_name = to_edit_element.querySelector(".measure_uom_name").innerHTML;
    to_edit_measurement.measure_size = to_edit_element.querySelector(".measure_size").innerHTML;
    to_edit_measurement.barcode = to_edit_element.querySelector(".barcode").innerHTML;

    // const measure_element = measure_edit.parentElement;
    // product_id = document.querySelector("#product_id");
    // relative_id = measure_element.querySelector(".relative_id");
    // measure_uom_name = measure_element.querySelector(".measure_uom_name");
    // measure_size = measure_element.querySelector(".measure_size");
    // barcode = measure_element.querySelector(".barcode");

    // dialog.setAttribute("style", "display:block");

    let edit_dialog_product_id = document.querySelector("#edit_dialog_product_id");
    edit_dialog_product_id.value = to_edit_measurement.product_id;
    let edit_dialog_uom_name = document.querySelector("#edit_dialog_uom_name");
    edit_dialog_uom_name.value = to_edit_measurement.measure_uom_name;
    let edit_dialog_measure_size = document.querySelector("#edit_dialog_measure_size");
    edit_dialog_measure_size.value = to_edit_measurement.measure_size;
    let edit_dialog_barcode = document.querySelector("#edit_dialog_barcode");
    edit_dialog_barcode.value = to_edit_measurement.barcode;
    let edit_measure_relative_id = document.querySelector("#edit_measure_relative_id");
    edit_measure_relative_id.value = to_edit_measurement.relative_id;

    // only one of them available to edit.
    if(edit_dialog_uom_name.value != ""){
        edit_measure_relative_id.disabled = true;
        
    } else if(edit_measure_relative_id.value != ""){
        // because uom is null, relative id able to set own value.
        edit_dialog_uom_name.disabled = true;
    }
}

function insertNewDefaultUomOption(relative_id){
    const default_uom_selection = document.querySelector("#product_default_relative_uom");
    
    let option = document.createElement("option");
    option.id = `option_${relative_id}`;
    option.value = relative_id;
    option.innerHTML = relative_id;
    
    default_uom_selection.appendChild(option);
}

function updateDefaultUomOption(old_relative_id, new_relative_id){
    const default_relative_id_selection = document.querySelector("#product_default_relative_uom");
    let option = default_relative_id_selection.querySelector(`#option_${old_relative_id}`);
    option.id = `option_${new_relative_id}`;
    option.value = new_relative_id;
    option.innerHTML = new_relative_id;
    
}


let measure_edit = document.querySelectorAll(".measure_edit");

function handle_measure_edit(edit){
    openDialog(edit.parentElement);    
}
measure_edit.forEach(edit =>{   
    edit.addEventListener("click", (e) =>{
        handle_measure_edit(e.target);
    });
})

// create measurement button event handler
let create_measure_button = document.querySelector("#create_measure_button");
create_measure_button.addEventListener("click", (e)=>{

    // check product id is valid.
    if(document.querySelector("#product_id").value == ""){
        alert("please fill product id.");
        document.querySelector("#product_id").required = true;
        return;
    }

    let measure_list = document.querySelector("#measurement_list");
    let newMeasure = document.createElement("div");
    if(measure_list.innerHTML == " "){
        measure_list.innerHTML = newMeasure;
    } else {
        measure_list.appendChild(newMeasure);
    }
    newMeasure.className = "row measure";

    let measure_uom_name = document.createElement("div");
    newMeasure.appendChild(measure_uom_name);
    measure_uom_name.className = "measure_uom_name";

    let measure_size = document.createElement("div");
    newMeasure.appendChild(measure_size);
    measure_size.className = "measure_size";

    let relative_id = document.createElement("div");
    newMeasure.appendChild(relative_id);
    relative_id.className = "relative_id";

    let barcode = document.createElement("div");
    newMeasure.appendChild(barcode);
    barcode.className = "barcode";

    let measure_edit = document.createElement("button");
    newMeasure.appendChild(measure_edit);
    measure_edit.className = "measure_edit tiny_button";
    measure_edit.textContent = "edit";
    measure_edit.addEventListener("click", (e)=>{
        handle_measure_edit(e.target);
    })

    measurement_is_creating = true;

    openDialog(newMeasure);
})