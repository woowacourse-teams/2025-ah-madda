const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const { DefinePlugin } = require('webpack');
const Dotenv = require('dotenv-webpack');

// .env 파일에서 환경변수 로드
require('dotenv').config();

module.exports = {
  mode: 'development',
  entry: './src/main.tsx',
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'dist'),
    clean: true,
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: './index.html',
      filename: 'index.html',
      inject: true,
    }),
    new DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV || 'development'),
      'process.env.REACT_APP_GOOGLE_CLIENT_ID': JSON.stringify(
        process.env.REACT_APP_GOOGLE_CLIENT_ID || ''
      ),
      'process.env.REACT_APP_GOOGLE_REDIRECT_URI': JSON.stringify(
        process.env.REACT_APP_GOOGLE_REDIRECT_URI || 'http://localhost:3000/auth/callback'
      ),
      'process.env.REACT_APP_SERVER_URL': JSON.stringify(
        process.env.REACT_APP_SERVER_URL || 'http://localhost:8000'
      ),
      VERSION: JSON.stringify('1.0.0'),
      __DEV__: JSON.stringify(true),
    }),
    new Dotenv({
      path: path.resolve(__dirname, '.env'),
      safe: true,
    }),
  ],
  module: {
    rules: [
      {
        test: /\.(ts|tsx)$/,
        use: ['babel-loader'],
        exclude: /node_modules/,
      },
      {
        test: /\.css$/,
        use: ['style-loader', 'css-loader'],
      },
      {
        test: /\.svg$/,
        issuer: /\.[jt]sx?$/,
        use: ['@svgr/webpack'],
      },
      {
        test: /\.(png|jpg|jpeg|gif)$/i,
        type: 'asset',
      },
    ],
  },

  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src/'),
    },
    extensions: ['.ts', '.tsx', '.js'],
  },
  devServer: {
    static: {
      directory: path.join(__dirname, 'dist'),
    },
    port: 5173,
    open: true,
    hot: true,
    historyApiFallback: true,
    client: {
      overlay: true,
    },
  },
};
