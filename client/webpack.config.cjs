const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const { DefinePlugin } = require('webpack');
const Dotenv = require('dotenv-webpack');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  mode: 'development',
  entry: './src/main.tsx',
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'dist'),
    publicPath: '/',
    clean: true,
  },
  plugins: [
    new HtmlWebpackPlugin({
      title: 'ah-madda',
      template: './index.html',
      filename: 'index.html',
      inject: true,
      templateParameters: {
        GOOGLE_ANALYTICS_ID: process.env.GOOGLE_ANALYTICS_ID || '',
      },
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
    new DefinePlugin({
      VERSION: JSON.stringify('1.0.0'),
      __DEV__: JSON.stringify(true),
    }),
    new Dotenv({
      path: path.resolve(
        __dirname,
        process.env.NODE_ENV === 'production' ? '.env.production' : '.env.development'
      ),
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
