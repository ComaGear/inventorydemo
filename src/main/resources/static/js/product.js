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

// async function selectOnChange(){
//     let select_list = document.querySelector("select#measurement_list");

//     if(select_list.options[select_list.selectedIndex].text == "new Measure"){
//         let measurement_name = document.querySelector("measurement_name");
//         let measure = document.querySelector("measure");
//         let barcode = document.querySelector("barcode");

//         measurement_name.value = "";
//         measure.value = "";
//         barcode.value = "";
//     }
    
    // select_list?
// }

let measure_edit = document.querySelector(".measure_edit");
measure_edit.addEventListener("click", (e)=>{
    const measure_element = measure_edit.parentElement;
    let product_id = document.querySelector("#product_id");
    let relative_id = measure_element.querySelector(".relative_id");
    let measure_uom_name = measure_element.querySelector(".measure_uom_name");
    let measure_size = measure_element.querySelector(".measure_size");
    let barcode = measure_element.querySelector(".barcode");

    let dialog = document.querySelector("#measure_edit_dialog");
    dialog.showModal();
    // dialog.setAttribute("style", "display:block");
    
    let measure_dialog_close = document.querySelector("#measure_dialog_close");
    measure_dialog_close.addEventListener("click", (e)=>{
        dialog.close();
    })

    let measure_dialog_save = document.querySelector("#measure_dialog_save");
    measure_dialog_save.addEventListener("click", (e)=>{
        dialog.close();

        // operation of update measure
    })

    let edit_dialog_product_id = document.querySelector("#edit_dialog_product_id");
    edit_dialog_product_id.value = product_id.value;
    let edit_dialog_uom_name = document.querySelector("#edit_dialog_uom_name");
    edit_dialog_uom_name.value = measure_uom_name.innerHTML;
    let edit_dialog_measure_size = document.querySelector("#edit_dialog_measure_size");
    edit_dialog_measure_size.value = measure_size.innerHTML;
    let edit_dialog_barcode = document.querySelector("#edit_dialog_barcode");
    edit_dialog_barcode.value = barcode.innerHTML;
});

// function displayMeasureEditPage(measure_element, measure_object){
    
// }

async function updateMeasure(){
    // TODO handle job about post a new measurement check <option> 'add Meas' is valid to post.
    // or update some measuremnt with put
}

let createButton = document.querySelector("button#create");
createButton?.addEventListener("click", createListener);
// let measurement_list = document.querySelector("#measurement_list");
// measurement_list?.addEventListener("change", selectOnChange);

