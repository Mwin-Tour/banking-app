// Configuration de l'API
const API_BASE_URL = 'http://localhost:8080/api';

// État de l'application
let currentClient = null;
let currentAccounts = [];

// ========== GESTION DES PAGES ==========

function showPage(pageId) {
    document.querySelectorAll('.page').forEach(page => {
        page.classList.remove('active');
    });
    document.getElementById(pageId).classList.add('active');
}

// ========== CONNEXION ==========

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const clientId = document.getElementById('clientId').value;
    const codePin = document.getElementById('codePin').value;
    const errorDiv = document.getElementById('loginError');
    
    try {
        const response = await fetch(`${API_BASE_URL}/clients/validate-pin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ clientId, codePin })
        });
        
        const data = await response.json();
        
        if (data.success) {
            currentClient = data.client;
            errorDiv.style.display = 'none';
            showToast('Connexion réussie !', 'success');
            loadDashboard();
        } else {
            errorDiv.textContent = data.message || 'Code PIN incorrect';
            errorDiv.style.display = 'block';
        }
    } catch (error) {
        console.error('Erreur:', error);
        errorDiv.textContent = 'Erreur de connexion. Vérifiez que le serveur est démarré.';
        errorDiv.style.display = 'block';
    }
});

// ========== INSCRIPTION ==========

document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const nom = document.getElementById('nom').value;
    const prenom = document.getElementById('prenom').value;
    const email = document.getElementById('email').value;
    const telephone = document.getElementById('telephone').value;
    const codePin = document.getElementById('newCodePin').value;
    const confirmPin = document.getElementById('confirmPin').value;
    
    const errorDiv = document.getElementById('registerError');
    const successDiv = document.getElementById('registerSuccess');
    
    // Validation
    if (codePin !== confirmPin) {
        errorDiv.textContent = 'Les codes PIN ne correspondent pas';
        errorDiv.style.display = 'block';
        successDiv.style.display = 'none';
        return;
    }
    
    if (!/^\d{4}$/.test(codePin)) {
        errorDiv.textContent = 'Le code PIN doit contenir exactement 4 chiffres';
        errorDiv.style.display = 'block';
        successDiv.style.display = 'none';
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/clients/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ nom, prenom, email, telephone, codePin })
        });
        
        const data = await response.json();
        
        if (data.success) {
            errorDiv.style.display = 'none';
            successDiv.innerHTML = `
                <strong>Inscription réussie !</strong><br>
                ${data.message}<br>
                Vous pouvez maintenant vous connecter.
            `;
            successDiv.style.display = 'block';
            
            // Réinitialiser le formulaire
            document.getElementById('registerForm').reset();
            
            // Rediriger vers la page de connexion après 3 secondes
            setTimeout(() => {
                showPage('loginPage');
                successDiv.style.display = 'none';
            }, 3000);
        } else {
            successDiv.style.display = 'none';
            errorDiv.textContent = data.message || 'Erreur lors de l\'inscription';
            errorDiv.style.display = 'block';
        }
    } catch (error) {
        console.error('Erreur:', error);
        successDiv.style.display = 'none';
        errorDiv.textContent = 'Erreur lors de l\'inscription. Vérifiez que le serveur est démarré.';
        errorDiv.style.display = 'block';
    }
});

// ========== DASHBOARD ==========

async function loadDashboard() {
    showPage('dashboardPage');
    document.getElementById('clientName').textContent = `${currentClient.prenom} ${currentClient.nom}`;
    
    await loadAccounts();
    await loadHistorique();
}

async function loadAccounts() {
    try {
        const response = await fetch(`${API_BASE_URL}/comptes/client/${currentClient.id}`);
        currentAccounts = await response.json();
        
        console.log('Comptes chargés:', currentAccounts);
        
        displayAccounts();
        updateAccountSelects();
    } catch (error) {
        console.error('Erreur lors du chargement des comptes:', error);
        showToast('Erreur lors du chargement des comptes', 'error');
    }
}

function displayAccounts() {
    const accountsList = document.getElementById('accountsList');
    
    if (currentAccounts.length === 0) {
        accountsList.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">💳</div>
                <p>Aucun compte</p>
                <p style="font-size: 0.9rem;">Créez votre premier compte bancaire</p>
            </div>
        `;
        return;
    }
    
    accountsList.innerHTML = currentAccounts.map(compte => `
        <div class="account-card" onclick="viewAccountDetails('${compte.numeroCompte}')">
            <div class="account-type">${compte.typeCompte === 'COURANT' ? '💳 Compte Courant' : '💰 Compte Épargne'}</div>
            <div class="account-numero">${compte.numeroCompte}</div>
            <div class="account-solde">${formatMoney(compte.solde, compte.devise)}</div>
            <div class="account-devise">${compte.devise}</div>
            <span class="account-statut">${compte.statut}</span>
        </div>
    `).join('');
}

function updateAccountSelects() {
    const selects = [
        'depotCompte', 
        'retraitCompte', 
        'transfertSource', 
        'historiqueCompteSelect'
    ];
    
    selects.forEach(selectId => {
        const select = document.getElementById(selectId);
        select.innerHTML = currentAccounts.map(compte => 
            `<option value="${compte.numeroCompte}" data-devise="${compte.devise}">${compte.numeroCompte} - ${formatMoney(compte.solde, compte.devise)}</option>`
        ).join('');
    });
    
    // Ajouter l'option "Tous les comptes" pour l'historique
    const historiqueSelect = document.getElementById('historiqueCompteSelect');
    historiqueSelect.innerHTML = '<option value="">Tous les comptes</option>' + historiqueSelect.innerHTML;
    
    // Ajouter les événements pour mettre à jour les labels de devise
    document.getElementById('depotCompte').addEventListener('change', updateDepotLabel);
    document.getElementById('retraitCompte').addEventListener('change', updateRetraitLabel);
    document.getElementById('transfertSource').addEventListener('change', updateTransfertLabel);
    
    // Mettre à jour les labels initialement
    updateDepotLabel();
    updateRetraitLabel();
    updateTransfertLabel();
}

function updateDepotLabel() {
    const select = document.getElementById('depotCompte');
    const devise = select.options[select.selectedIndex]?.dataset?.devise || 'EURO';
    const symbol = devise === 'FCFA' ? 'FCFA' : '€';
    document.getElementById('depotMontantLabel').textContent = `Montant (${symbol})`;
}

function updateRetraitLabel() {
    const select = document.getElementById('retraitCompte');
    const devise = select.options[select.selectedIndex]?.dataset?.devise || 'EURO';
    const symbol = devise === 'FCFA' ? 'FCFA' : '€';
    document.getElementById('retraitMontantLabel').textContent = `Montant (${symbol})`;
}

function updateTransfertLabel() {
    const select = document.getElementById('transfertSource');
    const devise = select.options[select.selectedIndex]?.dataset?.devise || 'EURO';
    const symbol = devise === 'FCFA' ? 'FCFA' : '€';
    document.getElementById('transfertMontantLabel').textContent = `Montant (${symbol})`;
}

function viewAccountDetails(numeroCompte) {
    const compte = currentAccounts.find(c => c.numeroCompte === numeroCompte);
    if (compte) {
        showToast(`Compte: ${numeroCompte} - Solde: ${formatMoney(compte.solde, compte.devise)}`, 'info');
    }
}

// ========== CRÉATION DE COMPTE ==========

document.getElementById('typeCompte').addEventListener('change', (e) => {
    const type = e.target.value;
    const decouvertGroup = document.getElementById('decouvertGroup');
    const tauxInteretGroup = document.getElementById('tauxInteretGroup');
    
    if (type === 'courant') {
        decouvertGroup.style.display = 'block';
        tauxInteretGroup.style.display = 'none';
    } else {
        decouvertGroup.style.display = 'none';
        tauxInteretGroup.style.display = 'block';
    }
});

function showCreateAccountModal() {
    document.getElementById('createAccountModal').style.display = 'block';
}

function closeCreateAccountModal() {
    document.getElementById('createAccountModal').style.display = 'none';
    document.getElementById('createAccountForm').reset();
    document.getElementById('createAccountError').style.display = 'none';
}

document.getElementById('createAccountModal').addEventListener('click', (e) => {
    if (e.target.id === 'createAccountModal') {
        closeCreateAccountModal();
    }
});

document.getElementById('createAccountForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const typeCompte = document.getElementById('typeCompte').value;
    const devise = document.getElementById('devise').value;
    const errorDiv = document.getElementById('createAccountError');
    
    let requestData = {
        clientId: currentClient.id,
        devise: devise
    };
    
    if (typeCompte === 'courant') {
        requestData.decouvert = parseFloat(document.getElementById('decouvert').value) || 0;
    } else {
        requestData.tauxInteret = parseFloat(document.getElementById('tauxInteret').value) || 2.5;
    }
    
    try {
        const endpoint = typeCompte === 'courant' ? '/comptes/courant' : '/comptes/epargne';
        
        console.log('Création de compte:', requestData); // Pour déboguer
        
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestData)
        });
        
        if (response.ok) {
            showToast('Compte créé avec succès !', 'success');
            closeCreateAccountModal();
            await loadAccounts();
        } else {
            const errorText = await response.text();
            const cleanMessage = extractErrorMessage(errorText);
            console.error('Erreur création compte:', errorText);
            errorDiv.textContent = cleanMessage;
            errorDiv.style.display = 'block';
        }
    } catch (error) {
        console.error('Erreur:', error);
        errorDiv.textContent = 'Erreur de connexion au serveur';
        errorDiv.style.display = 'block';
    }
});

// ========== OPÉRATIONS ==========

function showOperationTab(tabName) {
    // Désactiver tous les tabs
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    
    // Activer le tab sélectionné
    event.target.classList.add('active');
    document.getElementById(tabName + 'Tab').classList.add('active');
}

// Dépôt
document.getElementById('depotForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const numeroCompte = document.getElementById('depotCompte').value;
    const montant = parseFloat(document.getElementById('depotMontant').value);
    const description = document.getElementById('depotDescription').value || 'Dépôt';
    
    console.log('Dépôt:', { numeroCompte, montant, description });
    
    try {
        const response = await fetch(`${API_BASE_URL}/operations/credit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ numeroCompte, montant, description })
        });
        
        if (response.ok) {
            showToast('Dépôt effectué avec succès !', 'success');
            document.getElementById('depotForm').reset();
            await loadAccounts();
            await loadHistorique();
        } else {
            const errorText = await response.text();
            const cleanMessage = extractErrorMessage(errorText);
            console.error('Erreur dépôt:', errorText);
            showToast(cleanMessage, 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showToast('Erreur de connexion au serveur', 'error');
    }
});

// Retrait
document.getElementById('retraitForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const numeroCompte = document.getElementById('retraitCompte').value;
    const montant = parseFloat(document.getElementById('retraitMontant').value);
    const description = document.getElementById('retraitDescription').value || 'Retrait';
    
    console.log('Retrait:', { numeroCompte, montant, description });
    
    try {
        const response = await fetch(`${API_BASE_URL}/operations/debit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ numeroCompte, montant, description })
        });
        
        if (response.ok) {
            showToast('Retrait effectué avec succès !', 'success');
            document.getElementById('retraitForm').reset();
            await loadAccounts();
            await loadHistorique();
        } else {
            const errorText = await response.text();
            const cleanMessage = extractErrorMessage(errorText);
            console.error('Erreur retrait:', errorText);
            showToast(cleanMessage, 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showToast('Erreur de connexion au serveur', 'error');
    }
});

// Transfert
document.getElementById('transfertForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const numeroCompteSource = document.getElementById('transfertSource').value;
    const numeroCompteDest = document.getElementById('transfertDest').value;
    const montant = parseFloat(document.getElementById('transfertMontant').value);
    const description = document.getElementById('transfertDescription').value || 'Transfert';
    
    console.log('Transfert:', { numeroCompteSource, numeroCompteDest, montant, description }); // Pour déboguer
    
    try {
        const response = await fetch(`${API_BASE_URL}/operations/transfert`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ 
                numeroCompte: numeroCompteSource,
                numeroCompteDestinataire: numeroCompteDest,
                montant, 
                description 
            })
        });
        
        if (response.ok) {
            showToast('Transfert effectué avec succès !', 'success');
            document.getElementById('transfertForm').reset();
            await loadAccounts();
            await loadHistorique();
        } else {
            const errorText = await response.text();
            const cleanMessage = extractErrorMessage(errorText);
            console.error('Erreur transfert:', errorText);
            showToast(cleanMessage, 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showToast('Erreur de connexion au serveur', 'error');
    }
});

// ========== HISTORIQUE ==========

async function loadHistorique() {
    const numeroCompte = document.getElementById('historiqueCompteSelect').value;
    const historiqueList = document.getElementById('historiqueList');
    
    historiqueList.innerHTML = '<div class="loading"><div class="spinner"></div></div>';
    
    try {
        let operations = [];
        
        if (numeroCompte) {
            // Charger l'historique d'un compte spécifique
            const response = await fetch(`${API_BASE_URL}/operations/compte/${numeroCompte}`);
            operations = await response.json();
        } else {
            // Charger toutes les opérations de tous les comptes du client
            for (const compte of currentAccounts) {
                const response = await fetch(`${API_BASE_URL}/operations/compte/${compte.numeroCompte}`);
                const ops = await response.json();
                operations = operations.concat(ops);
            }
        }
        
        // Trier par date décroissante
        operations.sort((a, b) => new Date(b.dateOperation) - new Date(a.dateOperation));
        
        if (operations.length === 0) {
            historiqueList.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">📊</div>
                    <p>Aucune opération</p>
                </div>
            `;
            return;
        }
        
        historiqueList.innerHTML = operations.map(op => {
            const typeClass = op.typeOperation.toLowerCase();
            
            // Trouver le compte correspondant pour récupérer la devise
            const compte = currentAccounts.find(c => c.numeroCompte === op.numeroCompteSource);
            const devise = compte ? compte.devise : 'EURO';
            
            // Déterminer le signe et la classe en fonction du type d'opération
            let signe, montantClass;
            
            if (op.typeOperation === 'TRANSFERT') {
                // Pour un transfert, vérifier si on regarde le compte source ou destination
                // Si numeroCompte est rempli, on filtre sur un compte spécifique
                // Sinon, on regarde tous les comptes du client
                const isSource = currentAccounts.some(c => c.numeroCompte === op.numeroCompteSource);
                const isDestination = currentAccounts.some(c => c.numeroCompte === op.numeroCompteDestination);
                
                if (numeroCompte) {
                    // On affiche l'historique d'un compte spécifique
                    if (numeroCompte === op.numeroCompteSource) {
                        // C'est un débit (argent qui sort)
                        signe = '-';
                        montantClass = 'debit';
                    } else if (numeroCompte === op.numeroCompteDestination) {
                        // C'est un crédit (argent qui rentre)
                        signe = '+';
                        montantClass = 'credit';
                    } else {
                        signe = '-';
                        montantClass = 'debit';
                    }
                } else {
                    // On affiche tous les comptes
                    // Si les deux comptes sont au client, ne montrer qu'une fois (débit du source)
                    if (isSource && isDestination) {
                        signe = '↔';
                        montantClass = 'debit';
                    } else if (isSource) {
                        signe = '-';
                        montantClass = 'debit';
                    } else if (isDestination) {
                        signe = '+';
                        montantClass = 'credit';
                    } else {
                        signe = '-';
                        montantClass = 'debit';
                    }
                }
            } else {
                // Pour CREDIT et DEBIT, utiliser la logique normale
                montantClass = op.typeOperation === 'CREDIT' ? 'credit' : 'debit';
                signe = op.typeOperation === 'CREDIT' ? '+' : '-';
            }
            
            return `
                <div class="operation-item ${typeClass}">
                    <div class="operation-info">
                        <div class="operation-type">${getOperationTypeLabel(op.typeOperation)}</div>
                        <div class="operation-description">${op.description || 'Sans description'}</div>
                        <div class="operation-date">${formatDate(op.dateOperation)}</div>
                    </div>
                    <div class="operation-montant ${montantClass}">
                        ${signe}${formatMoney(op.montant, devise)}
                    </div>
                </div>
            `;
        }).join('');
        
    } catch (error) {
        console.error('Erreur lors du chargement de l\'historique:', error);
        historiqueList.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">❌</div>
                <p>Erreur lors du chargement</p>
            </div>
        `;
    }
}

// ========== UTILITAIRES ==========

function extractErrorMessage(errorText) {
    try {
        // Essayer de parser le JSON principal
        const errorObj = JSON.parse(errorText);
        
        if (errorObj.message) {
            const msg = errorObj.message;
            
            // Cas 1: Erreur Feign avec JSON imbriqué échappé
            // Format: [409] during [PUT] to [...]: [{\\"message\\":\\"Solde insuffisant\\",...}]
            const feignMatch = msg.match(/\[\{\\"message\\":\\"([^\\]+)\\"/);
            if (feignMatch && feignMatch[1]) {
                return feignMatch[1];
            }
            
            // Cas 2: Erreur Feign avec simple quotes
            const feignMatch2 = msg.match(/\[{"message":"([^"]+)"/);
            if (feignMatch2 && feignMatch2[1]) {
                return feignMatch2[1];
            }
            
            // Cas 3: Message simple sans imbrication
            if (!msg.includes('[') && !msg.includes('{')) {
                return msg;
            }
            
            // Cas 4: Retourner le message tel quel s'il est court
            if (msg.length < 100) {
                return msg;
            }
        }
        
        // Si on ne trouve rien de mieux, retourner le message original
        return errorObj.message || errorText;
        
    } catch (e) {
        // Si ce n'est pas du JSON valide, retourner tel quel
        return errorText;
    }
}

function formatMoney(amount, devise = 'EURO') {
    const formatted = new Intl.NumberFormat('fr-FR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(amount);
    
    // Affichage selon la devise
    if (devise === 'FCFA') {
        return formatted + ' FCFA';
    } else if (devise === 'EURO') {
        return formatted + ' €';
    }
    
    // Par défaut (si autre devise non reconnue)
    return formatted + ' ' + devise;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('fr-FR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }).format(date);
}

function getOperationTypeLabel(type) {
    const labels = {
        'CREDIT': '💰 Crédit',
        'DEBIT': '💸 Débit',
        'TRANSFERT': '🔄 Transfert'
    };
    return labels[type] || type;
}

function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type}`;
    toast.style.display = 'block';
    
    setTimeout(() => {
        toast.style.display = 'none';
    }, 3000);
}

function logout() {
    currentClient = null;
    currentAccounts = [];
    showPage('loginPage');
    document.getElementById('loginForm').reset();
    showToast('Déconnexion réussie', 'info');
}

// ========== INITIALISATION ==========

// Gérer la fermeture des modals avec la touche Escape
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        closeCreateAccountModal();
    }
});