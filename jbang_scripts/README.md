[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# JBang scripts
This directory contains [JBang](https://jbang.dev/) scripts that invoke AEM via the FluentForms REST Services APIs.

While being useful in and of themselves, they also provide example code for how to invoke the FluentForms REST Services.

### Sample JBang Invocations

###### Invoke AEM's Forms Service
`jbang ./invoke_forms.java -f ./sampleFiles/SampleForm.xdp -d ./sampleFiles/SampleForm_data.xml -o result_forms.pdf`

###### Invoke AEM's Output Service
`jbang ./invoke_output.java -f ./sampleFiles/SampleForm.xdp -d ./sampleFiles/SampleForm_data.xml -o result_output.pdf`