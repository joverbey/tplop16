.PHONY: all view aview clean

all: view

paper.pdf: $(wildcard *.tex) $(wildcard *.eps) $(wildcard *.bib)
	latex paper.tex
	bibtex paper
	latex paper.tex
	latex paper.tex
	dvips -Ppdf -Pdownload35 -j0 -G0 -tletter -opaper.ps paper.dvi
	ps2pdf \
		-sPAPERSIZE=letter \
		-dCompatibilityLevel=1.3 \
 		-dEmbedAllFonts=true \
		-dSubsetFonts=true \
		-dMaxSubsetPct=100 \
		-dAutoFilterColorImages=false \
		-dAutoFilterGrayImages=false \
		-dColorImageFilter=/FlateEncode \
		-dGrayImageFilter=/FlateEncode \
		-dMonoImageFilter=/FlateEncode  \
		paper.ps paperx.pdf
	pdftk paperx.pdf multibackground pagenums-pdf output paper.pdf
	rm -f paperx.pdf

view: paper.pdf
	OS=`uname -s`; \
	if [ "$$OS" == "Darwin" ]; then \
		open -a /Applications/Preview.app \
			paper.pdf; \
	else \
		evince paper.pdf; \
	fi

aview: paper.pdf
	OS=`uname -s`; \
	if [ "$$OS" == "Darwin" ]; then \
		open -a /Applications/Adobe\ Reader*/Adobe\ Reader*.app \
			paper.pdf; \
	else \
		acroread paper.pdf; \
	fi

clean:
	rm -f *.sch *.log *.aux *.dvi *.idx *.log *.pdf *.ps *.toc *.bbl *.blg
