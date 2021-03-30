import { Component, ElementRef, ViewChild } from '@angular/core';
import { Subscriber, Subscription } from 'rxjs';
import { HttpService } from 'src/http.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  public formPolicyDTO = [
    {
      formName: "SUSPENSE",
      policyIds: [

      ]
    },
    {
      formName: "AGENT",
      policyIds: [

      ]
    }, {
      formName: "INSURED",
      policyIds: [

      ]
    },
  ];

  public suspense = [
    {
      policyId: 1,
      label: 'ASCA0616934',
      isChecked: false
    }, {
      policyId: 2,
      label: 'ASCA0616935',
      isChecked: false
    }, {
      policyId: 3,
      label: 'ASCA06169346',
      isChecked: false
    }
  ]
  public agent = [
    {
      policyId: 3,
      label: 'ASCA0616935',
      isChecked: false
    }, {
      policyId: 4,
      label: 'ASCA0616936',
      isChecked: false
    }
  ]
  public insured = [
    {
      policyId: 3,
      label: 'ASCA0616936',
      isChecked: false
    }, {
      policyId: 4,
      label: 'ASCA0616937',
      isChecked: false
    }, {
      policyId: 5,
      label: 'ASCA0616938',
      isChecked: false
    }
  ]

  selectedSuspense = [];
  selectedAgent = [];
  selectedInsured = [];

  public disableButton: boolean = false;
  public requestId;
  public isUtilityStart: boolean = false;

  public getRequestIdandGeneratePDFSubscription: Subscription;
  public checkStatusAndDownloadPDFSubscription: Subscription;

  @ViewChild('userMsgOpenRef') userMsgOpenRef: ElementRef;
  @ViewChild('userMsgCloseRef') userMsgCloseRef: ElementRef;

  constructor(private httpService: HttpService) {
  }

  ngOnInit() {
  }

  ngOnDestroy() {
    if (this.getRequestIdandGeneratePDFSubscription) {
      this.getRequestIdandGeneratePDFSubscription.unsubscribe();
    }
    if (this.checkStatusAndDownloadPDFSubscription) {
      this.checkStatusAndDownloadPDFSubscription.unsubscribe();
    }
  }

  collectPolicyIds() {
    this.suspense.forEach(value => {
      if (value.isChecked) {
        this.selectedSuspense.push(value.policyId);
      }
    })

    this.agent.forEach(value => {
      if (value.isChecked) {
        this.selectedAgent.push(value.policyId);
      }
    })

    this.insured.forEach(value => {
      if (value.isChecked) {
        this.selectedInsured.push(value.policyId);
      }
    })

    let length = this.selectedSuspense.length + this.selectedAgent.length + this.selectedInsured.length
    if (length == 0) {
      alert("Please select the policy")
    } else {
      this.disableButton = true;

      this.formPolicyDTO.forEach((element, index) => {
        if (element.formName == 'SUSPENSE') {
          element.policyIds = this.selectedSuspense;
        } else if (element.formName == 'INSURED') {
          element.policyIds = this.selectedInsured;
        } else if (element.formName == 'AGENT') {
          element.policyIds = this.selectedAgent;
        }
      })
      const formPolicyDTOObject = {};
      formPolicyDTOObject["formPolicyDTO"] = this.formPolicyDTO;
      console.log("formPolicyDTO-->", JSON.stringify(formPolicyDTOObject));
      this.getRequestIdandGeneratePDF(formPolicyDTOObject);
    }
  }


  getRequestIdandGeneratePDF(formPolicyDTOObject) {
    console.log("AppComponent.getRequestIdandGeneratePDF: Started");
    console.log("AppComponent.getRequestIdandGeneratePDF: formPolicyDTOObject:", formPolicyDTOObject);

    this.httpService.generatePDF(formPolicyDTOObject).subscribe(response => {
      console.log("AppComponent.getRequestIdandGeneratePDF:Response- ", response.body);
      this.userMsgOpenRef.nativeElement.click();
      if (response.body.responseObject.code == 200) {
        this.requestId = response.body.responseObject.object;
        this.selectedSuspense = [];
        this.selectedAgent = [];
        this.selectedInsured = [];

        setTimeout(() => {
          if (this.requestId !== undefined && this.requestId !== null) {
            this.checkStatusAndDownloadPDF(this.requestId);
          }
        }, 60000);
      } else if (response.body.responseObject.code == 400) {
        alert('Please retry');
      }

    }, error => {
      this.disableButton = false;
      alert('Please retry');
      this.utility('')
      console.log("AppComponent.getRequestIdandGeneratePDF:Error:", error);
      this.selectedSuspense = [];
      this.selectedAgent = [];
      this.selectedInsured = [];
    }, () => {
      this.selectedSuspense = [];
      this.selectedAgent = [];
      this.selectedInsured = [];
      console.log("AppComponent.getRequestIdandGeneratePDF: Completed");
    })
  }

  checkStatusAndDownloadPDF(requestId) {
    console.log("AppComponent.getRequestIdandGeneratePDF: Started: Parameter - requestId :", requestId);

    this.httpService.checkStatusAndDownloadPDF(requestId).subscribe(response => {
      console.log("AppComponent.checkStatusAndDownloadPDF:Response- ", response);

      if(response.status == 200){

        var headers = response.headers;
        console.log("headers", headers);
        if (headers !== undefined) {
          var contentDisposition = headers.get('Content-Disposition');
          if(contentDisposition !== null && contentDisposition !== undefined){
            console.log("contentDisposition", contentDisposition);
  
            var indexof1 = contentDisposition.indexOf('=');
            var indexof2 = contentDisposition.indexOf('.');
            var contentDisposition = contentDisposition.substring(indexof1 + 1, indexof2);
            console.log("contentDisposition", contentDisposition);
            this.createAndDownloadPDF(response.body, contentDisposition);
          this.isUtilityStart = true;
          this.selectedSuspense = [];
          this.selectedAgent = [];
          this.selectedInsured = [];
          } else{
            this.utility(this.requestId)
          }
         
      
        } else{
          alert("Please retry");
        this.disableButton = false;

        }
  
        
      } else if(response.status == 400){
        this.utility(this.requestId)
      }
  

    }, error => {
      console.log("AppComponent.checkStatusAndDownloadPDF:Error:", error);
     
      this.disableButton = false;
      alert('Please retry')
      this.selectedSuspense = [];
      this.selectedAgent = [];
      this.selectedInsured = [];
    }, () => {
      this.selectedSuspense = [];
      this.selectedAgent = [];
      this.selectedInsured = [];
      console.log("AppComponent.checkStatusAndDownloadPDF: Completed");
    })
  }

  public createAndDownloadPDF(response, file = '') {
    console.log("AppComponent.createAndDownloadPDF: Started");

    let blob = new Blob([response], { type: 'application/pdf' });
    let link = document.createElement('a');
    link.setAttribute('href', 'data:application/pdf;charset=utf-8,' + encodeURIComponent(response._body));
    link.href = window.URL.createObjectURL(blob);
    link.download = file + '.pdf';
    var pom = document.createElement('a');
    document.body.appendChild(pom);
    pom.href = window.URL.createObjectURL(blob);
    pom.download = file + '.pdf';
    
      pom.target = "_self";
      pom.click();
      this.disableButton = false;
      this.userMsgCloseRef.nativeElement.click();
    console.log("AppComponent.createAndDownloadPDF: PDF downloaded");
  }


  utility(requestId) {
    console.log("AppComponent.utility: Started");

    const minutes = 1;
    let timeInMilliSeconds = 60000 * minutes;
    const utility = setInterval(() => {
      this.checkStatusAndDownloadPDF(requestId);
      console.log("AppComponent.utility: Called  downloadPDF API");
      if (this.isUtilityStart) {
        clearTimeout(utility);
      }
    }, timeInMilliSeconds);
  }

 
}
