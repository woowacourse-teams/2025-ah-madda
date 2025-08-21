const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpack = require('webpack');
const Dotenv = require('dotenv-webpack');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const { sentryWebpackPlugin } = require('@sentry/webpack-plugin');

module.exports = {
  entry: './src/main.tsx',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: '[name].[contenthash].js',
    publicPath: '/',
    clean: true,
  },
  plugins: [
    new HtmlWebpackPlugin({
      title: 'ah-madda',
      template: './index.html',
      filename: 'index.html',
      inject: true,
    }),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: 'public',
          to: '',
          globOptions: {
            ignore: ['**/index.html'],
          },
        },
      ],
    }),
    new webpack.DefinePlugin({
      VERSION: JSON.stringify('1.0.0'),
      __DEV__: JSON.stringify(process.env.NODE_ENV !== 'production'),
    }),
    new Dotenv({
      path: path.resolve(
        __dirname,
        process.env.NODE_ENV === 'production' ? '.env.production' : '.env.development'
      ),
      safe: true,
      systemvars: true,
    }),
    sentryWebpackPlugin({
      authToken: process.env.SENTRY_AUTH_TOKEN,
      org: 'ahmadda',
      project: 'ahmadda',
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
        test: /\.(png|jpg|jpeg|gif|webp)$/i,
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
};
