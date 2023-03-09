module.exports = {
    devServer: {
        disableHostCheck: true,
        proxy: {
            '/userinfo': {
                target: 'http://localhost:9090',
                changeOrigin: false
            },
            '^/oauth2': {
                target: 'http://localhost:9090',
                changeOrigin: false
            },
            '/login': {
                target: 'http://localhost:9090',
                changeOrigin: false
            },
            '/logout': {
                target: 'http://localhost:9090',
                changeOrigin: false
            },
            '^/api': {
                target: 'http://localhost:9090',
                changeOrigin: false
            }
        }
    }
}