const baseUrl = 'http://192.168.50.85:8080/palm-insurance/'

export const environment = {
  production: true,

  generatePDF: baseUrl + 'generatePDF',
  downloadPDF: baseUrl + 'downloadPdfByRequestId?requestId='


};
