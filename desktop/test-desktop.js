const { app, BrowserWindow } = require('electron');
const path = require('path');
const fs = require('fs');

app.whenReady().then(async () => {
  const win = new BrowserWindow({
    width: 1280,
    height: 800,
    show: false, // offscreen
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true
    }
  });

  win.loadFile(path.join(__dirname, 'index.html'));

  win.webContents.on('did-finish-load', async () => {
    console.log('DOM loaded successfully!');
    // Wait 1.5s for fonts & state render
    setTimeout(async () => {
      try {
        const image = await win.capturePage();
        const screenshotPath = path.join('C:\\Users\\Coraj\\.gemini\\antigravity\\brain\\b12d656e-5491-4c1a-801d-d2a643284a42', 'desktop_pc_screen.png');
        fs.writeFileSync(screenshotPath, image.toPNG());
        console.log('Desktop screenshot saved to:', screenshotPath);
      } catch (err) {
        console.error('Screenshot capture failed:', err);
      } finally {
        app.quit();
      }
    }, 1500);
  });

  win.webContents.on('render-process-gone', (e, details) => {
    console.error('Renderer crash:', details);
    app.exit(1);
  });
});
