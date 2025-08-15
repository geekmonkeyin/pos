// File: inventory-utils.js


function fetchImages(images,divId){
    var imageList = [];
     $.ajax({
            url: '/v1/inventory/images',
            method: 'POST',
            data: images.toString(),
            contentType: 'application/json',
            success: function (images) {
                images.forEach(function (image) {
                    const img = 'data:image/png;base64,' + image;
                    const imgCreated = document.createElement('img');
                    imgCreated.src = img;
                    document.getElementById(divId).append(imgCreated);
                });



            },
            error: function () {
                imageContainer.append('<p>Error loading images</p>');
                modal.modal('show');
            }
        });
}


function openImageModal(images,imageContainerId) {
    const modal = $('#imageModal');
    const imageContainer = modal.find(imageContainerId);
    imageContainer.empty();

    $.ajax({
        url: '/v1/inventory/images',
        method: 'POST',
        data: images.toString(),
        contentType: 'application/json',
        success: function (images) {
            images.forEach(function (image) {
                const img = $('<img>').attr('src', 'data:image/png;base64,' + image).addClass('img-fluid');
                imageContainer.append(img);
            });
            modal.modal('show');
        },
        error: function () {
            imageContainer.append('<p>Error loading images</p>');
            modal.modal('show');
        }
    });
}

function updateProductIdViaAPI(upcId, inventoryId) {
    if (inventoryId && upcId) {
        $.ajax({
            url: '/v1/inventory/updateProductId/' + upcId + '/' + inventoryId,
            method: 'GET',
            contentType: 'application/json',
            success: function () {
                alert('Product ID updated successfully');
                location.reload(); // Reload the page to reflect changes
            },
            error: function () {
                alert('Error updating Product ID');
            }
        });
    } else {
        alert('Please enter all required fields');
    }
}

function deleteProduct(upcId) {
    if (confirm('Are you sure you want to delete this product?')) {
        updateProductIdViaAPI(upcId, "recordnotrequired");
    }
}