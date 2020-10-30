.PHONY: clean wheel

dist: clean wheel

wheel: clean
	python3 setup.py bdist_wheel

# clean:
# 	\rm -rf dist build datarobot_drum.egg-info
# 	find . -type d -name __pycache__ | xargs rm -rf
# 	find . -name *.pyc -delete
