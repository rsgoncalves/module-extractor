# module-extractor


==Summary==
*OWL-ME: OWL Module Extractor* is a standalone application designed to extract different types of [http://owl.cs.manchester.ac.uk/research/topics/modularity/ Locality-based modules] from OWL ontologies.

The application takes an input ontology and signature file, and extracts a module for the specified signature onto a chosen location.

*OWL-ME* is implemented in Java, using the [http://owlapi.sourceforge.net/ OWL API] libraries.

----
==Signatures for module extraction==
Signature files should contain entity names as they appear in the original ontology.

Entity names can be separated by any of the following delimiters:
  * Commas (e.g. CSV files)
  * White spaces
  * Vertical bars "|"
  * Tabs
  * New lines

The file may also contain headers or comments, so long as the line or part thereof is preceded with '%'. All text following '%' is ignored.

_*Example signature file*_ <br>
% My header<br>
Class_A, Class_B <br>
Class_C Class_C<br>
Property_R | Property_S    % My properties<br>
<br>
% Some comment<br>
Class_D<br>
Class_F<br>

----
==SNOMED CT==
The module extractor accepts signature files for the SNOMED CT ontology in the UMLS Core Subset format. Any manually constructed signature files should have the concept ID's delimited by vertical bars "|", in a similar way as the UMLS Core Subset files.

----
==Questions/Bugs==
Any questions you may have feel free to e-mail them. Do consider checking the [http://owl.cs.manchester.ac.uk OWL@Manchester] webpage (and linked publications) for more information regarding _Locality-based_ modules, before submitting your queries.

If you come across any bugs please use the appropriate "Issues" tab to describe the problem, along with sufficient data to reproduce it (i.e. the ontology, or subset thereof, and the signature used).