# Thesis-JHipster

# Jhipster: A case study for variability techniques 

## Motivation

Jhipster is a configurable system that basically generates a Web stack (client and server sides included) depending on the options selected by users (typically people that want to develop Web sites/applications).

The project is quite popular and complex. It relies on a preprocessor for implementing variability and there is a configurator (in the console). 
It is an interesting case study due to the: 
  * popularity/maturity of the project: numerous contributions (pull requests, bugs, messages in mailing lists, etc.) trackable on github and other places with months of evolution 
  * the complexity: it is not the size/complexity of Linux but still it remains a medium-size project with non trivial features. The complexity is also quite tractable so that we can perhaps consider to *fully* generate all variants and thus collect *ground truths*. Overall a good compromise.  
  * the artefacts are Java/JavaScript/CSS/HTML, not the traditional C/C++ artefacts with cpp; build system is also based on maven/gradle/... not the traditional Makefile.  

A case study for:
 * sampling algorithms 
 * performance prediction
 * re-engineer a configurator 
 * testing variability (eg mutation) 
 * instrumeting a testing infrastructure for configurable systems 
 * ... 

## How to proceeed (roadmap)? 

A homework for MSc students (research-oriented curiculum) is available here: 
https://docs.google.com/document/d/1VIi2YIb1j4z3Jh1ER2qS064-Oim2T09dyRh5W9YfzVo/edit?usp=sharing
(the Google doc is currently visible only to people having the link, so people of this private repo ;)) 
This homework can be seen as a preliminary roadmap for conducting our research. 

A crucial step is to **automate** the derivation (incl. deployment) of Jhipster variants. 
That is, we need to instrument a derivation process with a feature model (for modeling the configurations) and a workflow that, based on a feature configuration, generates the instructions (including the "glue") for compiling/deploying/testing a Jhipster variant. 

*Per se*, it is an interesting subject since there are many users' laments and a lack of documentation related to some hidden steps that are in fact needed for an actual deployment of Jhipster. (The chance to have a variant of Jhipster working in the first place is not 100%). Hence we can certainly improve the situation and generate better documentation, recommend some actions, or even provide a script for fully launching a Jhipster variant. 
More generally, engineering this instrumented process can help us to investigate other issues (eg cost-effectiveness of sampling algorithms) in the large. 
