const sharp = require('sharp');
const src = 'D:/BISNIS/SEPATUMU/adhl-backend/adhl-backend/public/images/icon-512.png';
const densities = {
  'mdpi': 48,
  'hdpi': 72,
  'xhdpi': 96,
  'xxhdpi': 144,
  'xxxhdpi': 192
};

(async () => {
  for (const [dir, size] of Object.entries(densities)) {
    await sharp(src).resize(size, size).png()
      .toFile(`app/src/main/res/mipmap-${dir}/ic_launcher.png`);
    await sharp(src).resize(size, size).png()
      .toFile(`app/src/main/res/mipmap-${dir}/ic_launcher_round.png`);
    console.log(`OK ${dir} (${size}x${size})`);
  }
  console.log('Done!');
})();
