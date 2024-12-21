const TerserPlugin = require("terser-webpack-plugin");
const HtmlWebpackPlugin = require('html-webpack-plugin')
const path = require('path');
const webpack = require('webpack');

module.exports = {
    entry: './ui/index.js',
    mode: "production",
    output: {
        path: path.resolve(__dirname, 'target/classes/public/'),
        filename: 'bundle.min.js',
        libraryTarget: 'umd'
    },

    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /(node_modules|bower_components|build)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", '@babel/preset-react']
                    }
                }
            },
            {
                test: /\.(css|scss|sass)$/,
                use: ['style-loader', 'css-loader', 'sass-loader'],
            }
        ]
    },

    optimization: {
        minimize: true,
        minimizer: [
          new TerserPlugin({
            extractComments: true,
          }),
        ],
    },

    plugins: [
        new webpack.DefinePlugin({
            "process.env": {
                NODE_ENV: JSON.stringify("production")
            }
        }),
        
        new HtmlWebpackPlugin({
            filename: 'index.html',
            template: 'ui/index.html'
        })
    ]
}
