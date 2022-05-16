var accounts = '//localhost:9000/account'
const app = Vue.createApp({

    data() {
        return {
            account: new Object()
        };
    },



    methods: {

        
        addAccount() {
            axios.post(accounts, this.account )
                    .then(response => {
                        
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

