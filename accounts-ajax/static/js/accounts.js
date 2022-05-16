var accountsGraphQL = '//localhost:8082/graphql'
const app = Vue.createApp({

    data() {
        return {
            accounts: new Array(),
            account: new Object()
        };
    },

    mounted() {
        this.getAccounts();

    },

    methods: {
        getAccounts() {
            axios.post(accountsGraphQL, {query:'{accounts}'})
                    .then(response => {
                        this.accounts = response.data;
                    })
                    .catch(error => {
                        console.error(error);
                        alert("An error occurred - check the console for details.");
                    });
        },
        
        addAccount() {
            axios.post(accountsGraphQL, {query:'{addAccount(newAccount: account}'})
                    .then(response => {
                        this.getAccounts();
                    })
                    .catch(error => {
                        console.error(error);
                        alert("An error occurred - check the console for details.");
                    });
        }

    }

});

// mount the page at the <main> tag - this needs to be the last line in the file
app.mount("main");

