const { merge } = require('webpack-merge');
const common = require('./webpack.common.cjs');
const TerserPlugin = require('terser-webpack-plugin');

const path = require('path');
const crypto = require('crypto');

class RenameWithContentHashPlugin {
  /**
   * @param {string[]} files
   */
  constructor(files) {
    this.files = files;
  }
  apply(compiler) {
    const { RawSource } = compiler.webpack.sources;

    compiler.hooks.thisCompilation.tap('RenameWithContentHashPlugin', (compilation) => {
      compilation.hooks.processAssets.tap(
        {
          name: 'RenameWithContentHashPlugin',
          stage: compiler.webpack.Compilation.PROCESS_ASSETS_STAGE_OPTIMIZE,
        },
        (assets) => {
          const replacements = new Map();

          for (const filename of this.files) {
            const asset = compilation.getAsset(filename);
            if (!asset) continue;

            const src = asset.source.source();
            const buf = Buffer.isBuffer(src) ? src : Buffer.from(src);
            const hash = crypto.createHash('sha256').update(buf).digest('hex').slice(0, 8);

            const ext = path.extname(filename);
            const base = path.basename(filename, ext);
            const newName = `${base}.${hash}${ext}`;

            compilation.emitAsset(newName, asset.source, asset.info);
            compilation.deleteAsset(filename);

            replacements.set(filename, newName);
          }

          for (const target of ['index.html', 'manifest.json']) {
            const a = compilation.getAsset(target);
            if (!a) continue;

            let text = a.source.source().toString();
            for (const [oldName, newName] of replacements) {
              const re = new RegExp(oldName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'g');
              text = text.replace(re, newName);
            }
            compilation.updateAsset(target, new RawSource(text));
          }
        }
      );
    });
  }
}

module.exports = merge(common, {
  mode: 'production',
  output: {
    filename: '[name].[contenthash].js',
    chunkFilename: '[name].[contenthash].chunk.js',
    assetModuleFilename: 'assets/[name].[contenthash][ext]',
  },
  devtool: 'source-map',
  optimization: {
    splitChunks: { chunks: 'all' },
    minimizer: [
      new TerserPlugin({
        terserOptions: {
          compress: { drop_console: true },
          mangle: true,
        },
      }),
    ],
  },
  plugins: [
    new RenameWithContentHashPlugin([
      'favicon-dark.png',
      'favicon-light.png',
      'icon-192x192.png',
      'icon-512x512.png',
      'github.png',
      'main.png',
    ]),
  ],
});
