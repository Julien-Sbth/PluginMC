function buyItem(itemID, itemPrice, buttonElement) {
    console.log("ID de l'item:", itemID);
    console.log("Prix de l'item:", itemPrice);

    fetch("/api/user/panier/canbuy?buyItemID=" + itemID + "&itemPrice=" + itemPrice, {
        credentials: 'same-origin'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la vérification de l'achat: " + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            if (data.canBuy) {
                console.log("L'achat est possible.");
                confirmPurchase(itemID, buttonElement);
            } else {
                console.log("Solde de coins insuffisant pour acheter l'item.");
                alert("Solde de coins insuffisant pour acheter l'item.");
            }
        })
        .catch(error => {
            console.error("Erreur: ", error);
            alert("Une erreur s'est produite lors de la vérification de l'achat.");
        });
}

function confirmPurchase(itemID, buttonElement) {
    fetch("/api/user/panier/buy", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            buyItemID: itemID
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de l'achat de l'item: " + response.statusText);
            } else {
                console.log("Achat réussi pour l'item avec l'ID:", itemID);
                buttonElement.closest('.blocks').remove();
                alert("Achat réussi pour l'item avec l'ID: " + itemID);
            }
        })
        .catch(error => {
            console.error("Erreur: ", error);
            alert("Une erreur s'est produite lors de l'achat de l'item.");
        });
}

const images = document.querySelectorAll('.grid-item img, .blocks2 img');

images.forEach(image => {
    if (image.naturalWidth < image.parentNode.offsetWidth && image.naturalHeight < image.parentNode.offsetHeight) {
        image.style.width = '100%';
        image.style.height = '100%';
    }
});