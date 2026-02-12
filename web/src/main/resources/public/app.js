document.getElementById('convert-btn').addEventListener('click', async () => {
  const xml = document.getElementById('xml-input').value;
  const output = document.getElementById('json-output');

  try {
    const res = await fetch('/api/xml-to-json', {
      method: 'POST',
      headers: { 'Content-Type': 'application/xml' },
      body: xml
    });
    if (!res.ok) throw new Error('Server returned ' + res.status);
    const json = await res.json();
    output.textContent = JSON.stringify(json, null, 2);
  } catch (err) {
    output.textContent = 'Error: ' + err.message;
  }
});
