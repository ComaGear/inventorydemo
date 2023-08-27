// @ts-nocheck
const currentUrl = window.location.host;

async function createListener(){

    updateMeasure();

    let product_id = document.querySelector("#product_id")?.value;
    let product_name = document.querySelector("#product_name")?.value;
    let product_default_uom = document.querySelector("#product_uom")?.value;
    let active = document.querySelector("#active_yes")?.checked;

    const boby = JSON.stringify({
        product:{
            id: product_id,
            name : product_name,
            default_uom: product_default_uom,
            activity: active
        }
    });

    const url = "http://" + currentUrl + "/product/";
    const response = await fetch(url, {
        method : "POST",
        body: boby,
        headers : {
            "Content-Type": "application/json"
        },
    });

    response.json().then((data) =>{
        console.log(data)
    });
}

async function selectOnChange(){
    let select_list = document.querySelector("select#measurement_list");

    if(select_list.options[select_list.selectedIndex].text == "new Measure"){
        let measurement_name = document.querySelector("measurement_name");
        let measure = document.querySelector("measure");
        let barcode = document.querySelector("barcode");

        measurement_name.value = "";
        measure.value = "";
        barcode.value = "";
    }
    
    // select_list?
}

async function updateMeasure(){
    // TODO handle job about post a new measurement check <option> 'add Meas' is valid to post.
    // or update some measuremnt with put
}

let createButton = document.querySelector("button#create");
createButton?.addEventListener("click", createListener);
let measurement_list = document.querySelector("#measurement_list");
measurement_list?.addEventListener("change", selectOnChange);

