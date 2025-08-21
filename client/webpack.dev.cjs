const { merge } = require('webpack-merge');
const path = require('path');
const common = require('./webpack.common.cjs');

module.exports = merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  output: {
    filename: '[name].js',
  },
  devServer: {
    static: {
      directory: path.join(__dirname, 'public'),
    },
    historyApiFallback: true,
    port: 5173,
    hot: true,
    open: true,
  },
});
